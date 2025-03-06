package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lexeme.Companion.buildLexemeFromFormState
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.ui.uistate.AddLexemeFormEvent
import fr.steph.kanji.ui.uistate.AddLexemeFormState
import fr.steph.kanji.ui.utils.form_validation.ValidateField
import fr.steph.kanji.utils.extension.capitalized
import fr.steph.kanji.utils.extension.isLoneKanji
import fr.steph.kanji.utils.extension.kanaToRomaji
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddLexemeViewModel(
    lessonRepo: LessonRepository,
    lexemeRepo: LexemeRepository,
    apiRepo: ApiKanjiRepository,
) : ApiLexemeViewModel(lessonRepo, lexemeRepo, apiRepo) {

    private val _uiState = MutableStateFlow(AddLexemeFormState())
    val uiState = _uiState.asStateFlow()

    private var id = 0L
    private var unicode: String? = null

    fun onEvent(event: AddLexemeFormEvent) {
        when (event) {
            is AddLexemeFormEvent.LessonChanged -> {
                return _uiState.update { currentUiState ->
                    currentUiState.copy(lessonNumber = event.lessonNumber)
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
                unicode = event.kanji.unicode
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

    fun submitData() {
        val isCharactersLoneKanji = uiState.value.isCharactersLoneKanji
        val isCharactersLoneKanjiNotFetched = isCharactersLoneKanji && !uiState.value.isCharactersFetched

        if (isCharactersLoneKanjiNotFetched)
            return _uiState.update { currentUiState ->
                currentUiState.copy(charactersErrorRes = R.string.kanji_not_fetched_error)
            }

        _uiState.update { currentUiState ->
            currentUiState.copy(isSubmitting = true)
        }

        if (!isCharactersLoneKanji) {
            val charactersResult = ValidateField.validateCharacters(uiState.value.characters)
            val romajiResult = ValidateField.validateRomaji(uiState.value.romaji)
            val meaningResult = ValidateField.validateMeaning(uiState.value.meaning)

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    charactersErrorRes = charactersResult.errorMessageRes,
                    romajiErrorRes = romajiResult.errorMessageRes,
                    meaningErrorRes = meaningResult.errorMessageRes
                )
            }

            val hasError = listOf(charactersResult, romajiResult, meaningResult).any { !it.successful }

            if (hasError)
                return _uiState.update { currentUiState ->
                    currentUiState.copy(isSubmitting = false)
                }
        }

        val lexeme = buildLexemeFromFormState(id, uiState.value, unicode)
        upsertLexeme(lexeme)
    }
}