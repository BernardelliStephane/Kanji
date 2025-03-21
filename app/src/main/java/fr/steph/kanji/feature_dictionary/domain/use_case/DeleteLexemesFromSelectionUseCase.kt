package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.ui.util.LexemeResource
import fr.steph.kanji.core.ui.util.Resource

class DeleteLexemesFromSelectionUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(selection: List<Long>): LexemeResource {
        repository.deleteLexemesFromSelection(selection).let {
            return if (it != selection.size)
                Resource.Failure(R.string.room_deletion_failure)
            else Resource.Success()
        }
    }
}