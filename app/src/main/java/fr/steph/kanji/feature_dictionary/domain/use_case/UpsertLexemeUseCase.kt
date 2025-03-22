package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.INSERTION_FAILURE
import fr.steph.kanji.core.util.UPDATE_FAILURE
import fr.steph.kanji.feature_dictionary.domain.model.LexemeUpsertResult
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeState
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.validation.ValidateLexeme
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.validation.ValidationResult

class UpsertLexemeUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(uiState: AddLexemeState, lexemeId: Long, creationDate: Long): LexemeUpsertResult {

        val validationResults = validateFields(uiState)

        if (validationResults.any { !it.successful }) {
           return LexemeUpsertResult(
               lessonError = !validationResults[0].successful,
               charactersErrorRes = validationResults[1].error,
               romajiErrorRes = validationResults[2].error,
               meaningErrorRes = validationResults[3].error
           )
        }

        val (lexeme, failureValue) = when(uiState.isUpdating) {
            true -> uiState.toLexeme(lexemeId, creationDate) to UPDATE_FAILURE
            false -> uiState.toLexeme() to INSERTION_FAILURE
        }

        val result =
            if (uiState.isUpdating) repository.updateLexeme(lexeme)
            else repository.insertLexeme(lexeme)

        return if (result == failureValue)
            LexemeUpsertResult(upsertionResult = Resource.Failure(R.string.room_failure))
        else LexemeUpsertResult(upsertionResult = Resource.Success())
    }

    private fun validateFields(uiState: AddLexemeState): List<ValidationResult> {
        val lessonResult = ValidateLexeme.validateLesson(uiState.lessonNumber)

        val charactersResult = ValidateLexeme.validateCharacters(
            uiState.characters, uiState.isCharactersLoneKanji, uiState.isCharactersFetched
        )

        val romajiResult =
            if (uiState.isCharactersLoneKanji) ValidationResult(successful = true)
            else ValidateLexeme.validateRomaji(uiState.romaji)

        val meaningResult =
            if (uiState.isCharactersLoneKanji) ValidationResult(successful = true)
            else ValidateLexeme.validateMeaning(uiState.meaning)

        return listOf(lessonResult, charactersResult, romajiResult, meaningResult)
    }
}