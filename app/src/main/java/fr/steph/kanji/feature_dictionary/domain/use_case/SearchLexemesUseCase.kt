package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class SearchLexemesUseCase(private val repository: LexemeRepository) {

    operator fun invoke(query: String, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.searchLexemesOrderedByMeaning(query, sortOrder)
            SortField.LESSON_NUMBER -> repository.searchLexemesOrderedByLessonNumber(query, sortOrder)
            SortField.ROMAJI -> repository.searchLexemesOrderedByRomaji(query, sortOrder)
            SortField.ID -> repository.searchLexemesOrderedById(query, sortOrder)
        }
    }
}