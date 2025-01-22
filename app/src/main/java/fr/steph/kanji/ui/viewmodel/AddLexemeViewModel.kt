package fr.steph.kanji.ui.viewmodel

import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.data.utils.enumeration.LexemeType
import fr.steph.kanji.ui.form_presentation.AddLexemeFormEvent
import fr.steph.kanji.ui.form_presentation.AddLexemeFormState
import fr.steph.kanji.ui.form_presentation.validation.ValidateField
import fr.steph.kanji.utils.extension.capitalized
import fr.steph.kanji.utils.extension.isLoneKanji
import fr.steph.kanji.utils.extension.kanaToRomaji
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddLexemeViewModel(
    repo: LexemeRepository,
    apiRepo: ApiKanjiRepository,
) : ApiLexemeViewModel(repo, apiRepo) {

    private val _uiState = MutableStateFlow(AddLexemeFormState())
    val uiState = _uiState.asStateFlow()

    var id = 0

    fun onEvent(event: AddLexemeFormEvent) {
        when (event) {
            is AddLexemeFormEvent.CharactersChanged -> {
                return _uiState.update { currentUiState ->
                    currentUiState.copy(
                        characters = event.characters,
                        charactersErrorRes = null,
                        isCharactersLoneKanji = event.characters.isLoneKanji(),
                        isCharactersFetched = event.characters == currentUiState.lastFetchedKanji
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
                            romajiErrorRes = null,
                            meaning = meanings.joinToString().capitalized(),
                            meaningErrorRes = null,
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
        // TODO Verify entries before upserting lexeme
        val lexeme = Lexeme(id, LexemeType.KANA, _uiState.value.characters, _uiState.value.romaji, _uiState.value.meaning)
        upsertLexeme(lexeme)
    }
}