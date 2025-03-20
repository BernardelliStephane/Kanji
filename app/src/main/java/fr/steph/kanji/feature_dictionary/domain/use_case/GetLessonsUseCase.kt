package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LessonRepository
import fr.steph.kanji.core.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

class GetLessonsUseCase(private val repository: LessonRepository) {

    operator fun invoke(): Flow<List<Lesson>> {
        return repository.allLessons
    }
}