package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.domain.enumeration.SortField
import fr.steph.kanji.domain.enumeration.SortOrder
import fr.steph.kanji.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class SearchInFilteredLexemesUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(query: String, filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder)
            SortField.LESSON_NUMBER -> repository.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder)
            SortField.ROMAJI -> repository.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder)
            SortField.ID -> repository.searchInFilteredLexemesOrderedById(query, filter, sortOrder)
        }
    }
}