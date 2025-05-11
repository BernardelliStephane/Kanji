package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.LexemeWithLesson
import fr.steph.kanji.core.ui.util.LexemeResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.extension.capitalized
import fr.steph.kanji.core.util.extension.hasKanji
import fr.steph.kanji.core.util.extension.isLoneKanji
import fr.steph.kanji.core.util.extension.kanaToRomaji
import fr.steph.kanji.core.util.extension.log
import fr.steph.kanji.feature_dictionary.domain.use_case.AddLexemeUseCases
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLexemeViewModel(private val addLexemeUseCases: AddLexemeUseCases) : ViewModel() {

    val allLessons = addLexemeUseCases.getLessons().asLiveData()

    private val _uiState = MutableStateFlow(AddLexemeState())
    val uiState = _uiState.asStateFlow()

    private val _apiResponse = MutableSharedFlow<Resource<*>>(extraBufferCapacity = 1)
    val apiResponse = _apiResponse.asSharedFlow()

    private val validationEventChannel = Channel<LexemeResource>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    private var id = 0L
    private var creationDate: Long = 0
    private var addedLesson: Lesson? = null

    fun onEvent(event: AddLexemeEvent) {
        when (event) {
            is AddLexemeEvent.LessonAdded -> addedLesson = event.lesson

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
                    val charactersFetched = event.characters == currentUiState.lastFetch
                    // Update meaning depending on if the characters were fetched
                    val meaning = if (charactersFetched) currentUiState.lastFetchedKanjiMeaning
                        else if (currentUiState.isCharactersFetched) ""
                        else currentUiState.meaning

                    currentUiState.copy(
                        characters = event.characters,
                        charactersErrorRes = null,
                        meaning = meaning,
                        isCharactersContainingKanji = event.characters.hasKanji(),
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

            is AddLexemeEvent.Fetch -> viewModelScope.launch {
                if (uiState.value.isFetching) return@launch
                fetchCharacters(event.context, uiState.value.characters)
            }

            is AddLexemeEvent.Submit -> {
                val currentState = uiState.value

                if (!currentState.isUpdating)
                    return checkDuplicateCharacters(currentState, event.duplicateTranslationCallback)

                submitData()
            }
        }
    }

    private fun fetchCharacters(context: Context, characters: String) = viewModelScope.launch {
        _uiState.update { it.copy(isFetching = true) }

        val result =
            if (characters.length == 1) addLexemeUseCases.getKanjiInfo(context, characters)
            else addLexemeUseCases.getCompoundInfo(context, characters)

        if (result is Resource.Success)
            manageFetchedData(result.data!!, characters)

        _apiResponse.emit(result)

        _uiState.update { it.copy(isFetching = false) }
    }

    private fun manageFetchedData(data: Any, fetchedCharacters: String) {
        when (data) {
            is ApiKanji -> {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        characters = data.kanji,
                        charactersErrorRes = null,
                        romajiErrorRes = null,
                        meaning = data.meanings.joinToString().capitalized(),
                        meaningErrorRes = null,
                        lastFetchedKanjiMeaning = data.meanings.joinToString().capitalized(),
                        onyomi = data.onReadings.joinToString(),
                        onyomiRomaji = data.onReadings.joinToString { it.kanaToRomaji() },
                        kunyomi = data.kunReadings.joinToString(),
                        kunyomiRomaji = data.kunReadings.joinToString { it.kanaToRomaji() },
                        nameReadings = data.nameReadings.joinToString(),
                        nameReadingsRomaji = data.nameReadings.joinToString { it.kanaToRomaji() },
                        gradeTaught = data.gradeTaught?.toString() ?: "",
                        jlptLevel = data.jlptLevel?.toString() ?: "",
                        useFrequencyIndicator = data.useFrequency?.toString() ?: "",
                        lastFetch = data.kanji,
                        isCharactersFetched = data.kanji == currentUiState.characters
                    )
                }
            }

            is List<*> -> {
                log("Fetched data: $data")
                val allGlosses = (data as List<Word>).flatMap { it.meanings }.flatMap { it.glosses }
                val allRomaji = data.flatMap { it.variants }.map { it.pronounced.kanaToRomaji() }.distinct()
                val alternativeWritings = data.flatMap { it.variants }.map { it.written }.filterNot { it == fetchedCharacters }.distinct()

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        characters = fetchedCharacters,
                        charactersErrorRes = null,
                        alternativeWritings = alternativeWritings.joinToString(),
                        romaji = allRomaji.joinToString(),
                        romajiErrorRes = null,
                        meaning = allGlosses.joinToString().capitalized(),
                        meaningErrorRes = null,
                        lastFetchedKanjiMeaning = allGlosses.joinToString().capitalized(),
                        lastFetch = fetchedCharacters,
                        isCharactersFetched = fetchedCharacters == currentUiState.characters
                    )
                }
            }
        }
    }

    private fun checkDuplicateCharacters(uiState: AddLexemeState, duplicateCallback: (LexemeWithLesson) -> Unit) {
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

    fun updateUiFromLexeme(context: Context, lexemeWithLesson: LexemeWithLesson): Lesson {
        val lexeme = lexemeWithLesson.lexeme

        _uiState.update { lexeme.toAddLexemeFormState().copy(isUpdating = true) }
        if (lexeme.characters.hasKanji())
            fetchCharacters(context, lexeme.characters)

        id = lexeme.id
        creationDate = lexeme.creationDate

        return lexemeWithLesson.lesson
    }

    fun resetUi() {
        _uiState.update { AddLexemeState() }
        id = 0
        creationDate = 0
    }

    fun getAddedLesson() = addedLesson

    fun resetAddedLesson() {
        addedLesson = null
    }
}