package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

class GetLessonsUseCase(private val repository: LessonRepository) {

    operator fun invoke(): Flow<List<Lesson>> {
        return repository.allLessons
    }
}