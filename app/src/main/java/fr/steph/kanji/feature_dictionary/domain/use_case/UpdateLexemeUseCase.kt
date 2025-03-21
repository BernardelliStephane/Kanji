package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.LexemeViewModel.ValidationEvent
import fr.steph.kanji.core.ui.UPDATE_FAILURE

class UpdateLexemeUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(lexeme: Lexeme): ValidationEvent {
        repository.updateLexeme(lexeme).let {
            return if (it == UPDATE_FAILURE)
                ValidationEvent.Failure(R.string.room_failure)
            else ValidationEvent.Success
        }
    }
}