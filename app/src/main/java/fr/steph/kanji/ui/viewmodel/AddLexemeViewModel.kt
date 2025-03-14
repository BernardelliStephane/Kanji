package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.ui.uistate.AddLexemeFormEvent
import fr.steph.kanji.ui.uistate.AddLexemeFormState
import fr.steph.kanji.ui.utils.form_validation.ValidateLexemeField
import fr.steph.kanji.utils.extension.capitalized
import fr.steph.kanji.utils.extension.isLoneKanji
import fr.steph.kanji.utils.extension.kanaToRomaji
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLexemeViewModel(
    lessonRepo: LessonRepository,
    lexemeRepo: LexemeRepository,
    apiRepo: ApiKanjiRepository,
) : ApiLexemeViewModel(lessonRepo, lexemeRepo, apiRepo) {

    private val _uiState = MutableStateFlow(AddLexemeFormState())
    val uiState = _uiState.asStateFlow()

    private lateinit var lexemeList: List<Lexeme>

    private var id = 0L
    private var additionDate = 0L

    init {
        viewModelScope.launch {
            lexemes.asFlow().collect { lexemes ->
                lexemeList = lexemes
            }
        }
    }

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
        }
    }

    fun onSearchClicked() {
        if (lastKanjiFetch.value is Resource.Loading) return
        else getKanjiInfo(uiState.value.characters)
    }

    fun submitData(duplicateTranslationCallback: (Lexeme) -> Unit) {
        val currentState = uiState.value

        if (!currentState.isUpdating) {
            val lexemeCharacters = lexemeList.map { it.characters }
            val index = lexemeCharacters.indexOf(currentState.characters)
            if (index != -1)
                return duplicateTranslationCallback.invoke(lexemeList[index])
        }

        _uiState.update { currentUiState ->
            currentUiState.copy(isSubmitting = true)
        }

        val hasError: Boolean
        
        val lessonResult = ValidateLexemeField.validateLesson(currentState.lessonNumber)
        val charactersResult = ValidateLexemeField.validateCharacters(
            currentState.characters,
            currentState.isCharactersLoneKanji,
            currentState.isCharactersFetched
        )

        if (currentState.isCharactersLoneKanji) {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    lessonError = !lessonResult.successful,
                    charactersErrorRes = charactersResult.errorMessageRes
                )
            }

            hasError = listOf(lessonResult, charactersResult).any { !it.successful }
        }

        else {
            val romajiResult = ValidateLexemeField.validateRomaji(currentState.romaji)
            val meaningResult = ValidateLexemeField.validateMeaning(currentState.meaning)

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

        val lexeme = currentState.toLexeme(id)
        if (currentState.isUpdating) updateLexeme(lexeme)
        else insertLexeme(lexeme)
    }

    fun updateUi(lexeme: Lexeme): Int {
        _uiState.update { lexeme.toAddLexemeFormState().copy(isUpdating = true) }
        if (lexeme.characters.isLoneKanji())
            getKanjiInfo(lexeme.characters)

        id = lexeme.id
        additionDate = lexeme.additionDate

        return allLessons.value?.map { it.number }?.indexOf(lexeme.lessonNumber) ?: 0
    }

    fun stopSubmission() {
        _uiState.update { currentUiState ->
            currentUiState.copy(isSubmitting = false)
        }
    }
}