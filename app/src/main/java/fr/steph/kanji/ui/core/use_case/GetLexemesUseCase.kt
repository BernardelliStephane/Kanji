package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.domain.enumeration.SortField
import fr.steph.kanji.domain.enumeration.SortOrder
import fr.steph.kanji.domain.model.Lexeme
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