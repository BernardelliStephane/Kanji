package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class SearchInFilteredLexemesUseCase(private val repository: LexemeRepository) {

    operator fun invoke(query: String, filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.searchInFilteredLexemesOrderedByMeaning(query, filter, sortOrder)
            SortField.LESSON_NUMBER -> repository.searchInFilteredLexemesOrderedByLessonNumber(query, filter, sortOrder)
            SortField.ROMAJI -> repository.searchInFilteredLexemesOrderedByRomaji(query, filter, sortOrder)
            SortField.ID -> repository.searchInFilteredLexemesOrderedById(query, filter, sortOrder)
        }
    }
}