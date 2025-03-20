package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.domain.model.Lexeme
import kotlinx.coroutines.flow.Flow

class GetLexemesUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(sortField: SortField, sortOrder: SortOrder): Flow<List<Lexeme>> {
        return when (sortField) {
            SortField.MEANING -> repository.lexemesOrderedByMeaning(sortOrder)
            SortField.LESSON_NUMBER -> repository.lexemesOrderedByLessonNumber(sortOrder)
            SortField.ROMAJI -> repository.lexemesOrderedByRomaji(sortOrder)
            SortField.ID -> repository.lexemesOrderedById(sortOrder)
        }
    }
}