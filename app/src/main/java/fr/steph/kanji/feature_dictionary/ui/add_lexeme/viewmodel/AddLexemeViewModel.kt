package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.util.ApiResource
import fr.steph.kanji.core.ui.util.LexemeResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.extension.capitalized
import fr.steph.kanji.core.util.extension.isLoneKanji
import fr.steph.kanji.core.util.extension.kanaToRomaji
import fr.steph.kanji.feature_dictionary.domain.use_case.AddLexemeUseCases
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLexemeViewModel(private val addLexemeUseCases: AddLexemeUseCases) : ViewModel() {

    val allLessons = addLexemeUseCases.getLessons().asLiveData()

    private val _uiState = MutableStateFlow(AddLexemeState())
    val uiState = _uiState.asStateFlow()

    private val apiResponseChannel = Channel<ApiResource>()
    val apiResponse = apiResponseChannel.receiveAsFlow()

    private val validationEventChannel = Channel<LexemeResource>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    private var id = 0L
    private var creationDate: Long = 0

    fun onEvent(event: AddLexemeEvent) {
        when (event) {
            is AddLexemeEvent.LessonChanged -> {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        lessonNumber = event.lessonNumber,
                        lessonError = false
                    )
                }
            }

            is AddLexemeEvent.CharactersChanged -> {
                _uiState.update { currentUiState ->
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

            is AddLexemeEvent.RomajiChanged -> {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        romaji = event.romaji,
                        romajiErrorRes = null
                    )
                }
            }

            is AddLexemeEvent.MeaningChanged -> {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        meaning = event.meaning,
                        meaningErrorRes = null
                    )
                }
            }

            is AddLexemeEvent.KanjiFetched -> {
                _uiState.update { currentUiState ->
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

            is AddLexemeEvent.Fetch -> viewModelScope.launch {
                if (uiState.value.isFetching) return@launch
                fetchKanji(uiState.value.characters)
            }

            is AddLexemeEvent.Submit -> {
                val currentState = uiState.value

                if (!currentState.isUpdating)
                    return checkDuplicateCharacters(currentState, event.duplicateTranslationCallback)

                submitData()
            }
        }
    }

    private fun fetchKanji(characters: String) = viewModelScope.launch {
        _uiState.update { it.copy(isFetching = true) }

        val result = addLexemeUseCases.getKanjiInfo(characters)
        apiResponseChannel.send(result)

        _uiState.update { it.copy(isFetching = false) }
    }

    private fun checkDuplicateCharacters(uiState: AddLexemeState, duplicateCallback: (Lexeme) -> Unit) {
        addLexemeUseCases.getLexemeByCharacters(uiState.characters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { // Database error
                viewModelScope.launch {
                    validationEventChannel.send(Resource.Failure(R.string.room_failure))
                }
            }
            .doOnSuccess { // Matching lexeme found
                duplicateCallback.invoke(it)
            }
            .doOnComplete { // No matching lexeme found
                submitData()
            }
            .subscribe()
    }

    private fun submitData() = viewModelScope.launch {
        _uiState.update { it.copy(isSubmitting = true) }

        val result = addLexemeUseCases.upsertLexeme(uiState.value, id, creationDate)
        result.upsertionResult?.let {
            validationEventChannel.send(it)
        } ?: _uiState.update { currentUiState ->
            currentUiState.copy(
                lessonError = result.lessonError,
                charactersErrorRes = result.charactersErrorRes,
                romajiErrorRes = result.romajiErrorRes,
                meaningErrorRes = result.meaningErrorRes
            )
        }

        _uiState.update { it.copy(isSubmitting = false) }
    }

    fun updateUi(lexeme: Lexeme): Int {
        _uiState.update { lexeme.toAddLexemeFormState().copy(isUpdating = true) }
        if (lexeme.characters.isLoneKanji())
            fetchKanji(uiState.value.characters)

        id = lexeme.id
        creationDate = lexeme.creationDate

        return allLessons.value?.map { it.number }?.indexOf(lexeme.lessonNumber) ?: -1
    }

    fun resetUi() {
        _uiState.update { AddLexemeState() }
        id = 0
        creationDate = 0
    }
}