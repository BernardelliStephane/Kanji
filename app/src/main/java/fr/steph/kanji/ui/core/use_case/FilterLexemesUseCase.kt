package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.domain.enumeration.SortField
import fr.steph.kanji.domain.enumeration.SortOrder
import fr.steph.kanji.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class FilterLexemesUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.filterLexemesOrderedByMeaning(filter, sortOrder)
            SortField.LESSON_NUMBER -> repository.filterLexemesOrderedByLessonNumber(filter, sortOrder)
            SortField.ROMAJI -> repository.filterLexemesOrderedByRomaji(filter, sortOrder)
            SortField.ID -> repository.filterLexemesOrderedById(filter, sortOrder)
        }
    }
}