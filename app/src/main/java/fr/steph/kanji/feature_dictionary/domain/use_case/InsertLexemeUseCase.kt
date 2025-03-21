package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.util.LexemeResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.INSERTION_FAILURE


class InsertLexemeUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(lexeme: Lexeme): LexemeResource {
        repository.insertLexeme(lexeme).let {
            return if (it == INSERTION_FAILURE)
                Resource.Failure(R.string.room_failure)
            else Resource.Success()
        }
    }
}