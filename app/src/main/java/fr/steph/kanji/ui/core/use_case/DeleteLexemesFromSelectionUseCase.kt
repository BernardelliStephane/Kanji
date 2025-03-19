package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.R
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.ui.core.viewmodel.LexemeViewModel.ValidationEvent

class DeleteLexemesFromSelectionUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(selection: List<Long>): ValidationEvent {
        repository.deleteLexemesFromSelection(selection).let {
            return if (it != selection.size)
                ValidationEvent.Failure(R.string.room_deletion_failure)
            else ValidationEvent.Success
        }
    }
}