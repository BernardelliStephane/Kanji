package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class FilterLexemesUseCase(private val repository: LexemeRepository) {

    operator fun invoke(filter: List<Long>, sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.filterLexemesOrderedByMeaning(filter, sortOrder)
            SortField.LESSON_NUMBER -> repository.filterLexemesOrderedByLessonNumber(filter, sortOrder)
            SortField.ROMAJI -> repository.filterLexemesOrderedByRomaji(filter, sortOrder)
            SortField.ID -> repository.filterLexemesOrderedById(filter, sortOrder)
        }
    }
}