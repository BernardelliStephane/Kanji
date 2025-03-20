package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonsUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLexemeByCharactersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLexemeUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.UpdateLexemeUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeFormEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeFormState
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.validation.ValidateLexemeField
import fr.steph.kanji.core.ui.viewmodel.LexemeViewModel
import fr.steph.kanji.core.util.extension.capitalized
import fr.steph.kanji.core.util.extension.isLoneKanji
import fr.steph.kanji.core.util.extension.kanaToRomaji
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLexemeViewModel(
    private val getKanjiInfo: GetKanjiInfoUseCase,
    private val insertLexeme: InsertLexemeUseCase,
    private val updateLexeme: UpdateLexemeUseCase,
    private val getLexemeByCharacters: GetLexemeByCharactersUseCase,
    getLessons: GetLessonsUseCase,
) : ViewModel() {

    val allLessons = getLessons().asLiveData()

    private val _uiState = MutableStateFlow(AddLexemeFormState())
    val uiState = _uiState.asStateFlow()

    private val _lastKanjiFetch: MutableLiveData<Resource?> = MutableLiveData()
    val lastKanjiFetch: LiveData<Resource?> = _lastKanjiFetch

    private val lexemeValidationEventChannel = Channel<LexemeViewModel.ValidationEvent>()
    val lexemeValidationEvents = lexemeValidationEventChannel.receiveAsFlow()

    private var id = 0L
    private var additionDate = 0L

    fun onEvent(event: AddLexemeFormEvent) {
        when (event) {
            is AddLexemeFormEvent.LessonChanged -> {
                return _uiState.update { currentUiState ->
                    currentUiState.copy(
                        lessonNumber = event.lessonNumber,
                        lessonError = false)
                }
            }

            is AddLexemeFormEvent.CharactersChanged -> {
                return _uiState.update { currentUiState ->
                    val charactersFetched = event.characters == currentUiState.lastFetchedKanji
                    // Update meaning depending on if the characters were fetched
                    val meaning = if (charactersFetched) currentUiState.lastFetchedKanjiMeaning
                        else if (currentUiState.isCharactersFetched) ""
                        else currentUiState.meaning

                    currentUiState.copy(
                        characters = event.characters,
                        charactersErrorRes = null,
                        meaning = meaning,
                        isCharactersLoneKanji = event.characters.isLoneKanji(),
                        isCharactersFetched = charactersFetched,
                    )
                }
            }

            is AddLexemeFormEvent.RomajiChanged -> {
                return _uiState.update { currentUiState ->
                    currentUiState.copy(
                        romaji = event.romaji,
                        romajiErrorRes = null
                    )
                }
            }

            is AddLexemeFormEvent.MeaningChanged -> {
                return _uiState.update { currentUiState ->
                    currentUiState.copy(
                        meaning = event.meaning,
                        meaningErrorRes = null
                    )
                }
            }

            is AddLexemeFormEvent.KanjiFetched -> {
                return _uiState.update { currentUiState ->
                    event.kanji.run {
                        currentUiState.copy(
                            charactersErrorRes = null,
                            romajiErrorRes = null,
                            meaning = meanings.joinToString().capitalized(),
                            meaningErrorRes = null,
                            lastFetchedKanjiMeaning = meanings.joinToString().capitalized(),
                            onyomi = onReadings.joinToString(),
                            onyomiRomaji = onReadings.joinToString { it.kanaToRomaji() },
                            kunyomi = kunReadings.joinToString(),
                            kunyomiRomaji = kunReadings.joinToString { it.kanaToRomaji() },
                            nameReadings = nameReadings.joinToString(),
                            nameReadingsRomaji = nameReadings.joinToString { it.kanaToRomaji() },
                            gradeTaught = gradeTaught?.toString() ?: "",
                            jlptLevel = jlptLevel?.toString() ?: "",
                            useFrequencyIndicator = useFrequency?.toString() ?: "",
                            lastFetchedKanji = kanji,
                            isCharactersFetched = kanji == currentUiState.characters
                        )
                    }
                }
            }

            is AddLexemeFormEvent.Search -> viewModelScope.launch {
                if (lastKanjiFetch.value is Resource.Loading) return@launch

                val result = getKanjiInfo(uiState.value.characters)
                _lastKanjiFetch.postValue(result)
            }

            is AddLexemeFormEvent.Submit -> {
                val currentState = uiState.value

                if (currentState.isUpdating)
                    return submitData(currentState)

                checkDuplicateCharacters(currentState, event.duplicateTranslationCallback)
            }
        }
    }

    private fun checkDuplicateCharacters(form: AddLexemeFormState, duplicateCallback: (Lexeme) -> Unit) = viewModelScope.launch {
        getLexemeByCharacters(form.characters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { // Database error
                viewModelScope.launch {
                    lexemeValidationEventChannel.send(LexemeViewModel.ValidationEvent.Failure(R.string.room_failure))
                }
            }
            .doOnSuccess { // Matching lexeme found
                duplicateCallback.invoke(it)
            }
            .doOnComplete { // No matching lexeme found
                submitData(form)
            }
            .subscribe()
    }

    private fun submitData(form: AddLexemeFormState) {
        _uiState.update { currentUiState ->
            currentUiState.copy(isSubmitting = true)
        }

        val hasError: Boolean

        val lessonResult = ValidateLexemeField.validateLesson(form.lessonNumber)
        val charactersResult = ValidateLexemeField.validateCharacters(
            form.characters,
            form.isCharactersLoneKanji,
            form.isCharactersFetched
        )

        if (form.isCharactersLoneKanji) {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    lessonError = !lessonResult.successful,
                    charactersErrorRes = charactersResult.errorMessageRes
                )
            }

            hasError = listOf(lessonResult, charactersResult).any { !it.successful }
        }

        else {
            val romajiResult = ValidateLexemeField.validateRomaji(form.romaji)
            val meaningResult = ValidateLexemeField.validateMeaning(form.meaning)

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    lessonError = !lessonResult.successful,
                    charactersErrorRes = charactersResult.errorMessageRes,
                    romajiErrorRes = romajiResult.errorMessageRes,
                    meaningErrorRes = meaningResult.errorMessageRes
                )
            }

            hasError = listOf(lessonResult, charactersResult, romajiResult, meaningResult).any { !it.successful }
        }

        if (hasError)
            return _uiState.update { currentUiState ->
                currentUiState.copy(isSubmitting = false)
            }

        val lexeme = form.toLexeme(id)

        viewModelScope.launch {
            val result = if (form.isUpdating) updateLexeme(lexeme)
                else insertLexeme(lexeme)
            lexemeValidationEventChannel.send(result)
        }
    }

    fun updateUi(lexeme: Lexeme): Int {
        _uiState.update { lexeme.toAddLexemeFormState().copy(isUpdating = true) }
        if (lexeme.characters.isLoneKanji()) {
            viewModelScope.launch {
                val result = getKanjiInfo(uiState.value.characters)
                _lastKanjiFetch.postValue(result)
            }
        }

        id = lexeme.id
        additionDate = lexeme.additionDate

        return allLessons.value?.map { it.number }?.indexOf(lexeme.lessonNumber) ?: 0
    }

    fun resetUi() {
        _uiState.update { AddLexemeFormState() }
        id = 0
        additionDate = 0
    }

    fun stopSubmission() {
        _uiState.update { currentUiState ->
            currentUiState.copy(isSubmitting = false)
        }
    }

    sealed class Resource {
        data object Loading : Resource()
        data class Error(val message: Int) : Resource()
        data class Success(val data: ApiKanji) : Resource()
    }
}