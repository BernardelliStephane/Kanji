package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.R
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.domain.model.Lexeme
import fr.steph.kanji.ui.core.viewmodel.LexemeViewModel.ValidationEvent
import fr.steph.kanji.ui.core.viewmodel.UPDATE_FAILURE

class UpdateLexemeUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(lexeme: Lexeme): ValidationEvent {
        repository.updateLexeme(lexeme).let {
            return if (it == UPDATE_FAILURE)
                ValidationEvent.Failure(R.string.room_failure)
            else ValidationEvent.Success
        }
    }
}