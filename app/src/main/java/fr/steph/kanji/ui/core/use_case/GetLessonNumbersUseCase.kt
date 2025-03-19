package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LessonRepository
import kotlinx.coroutines.flow.Flow

class GetLessonNumbersUseCase(private val repository: LessonRepository) {

    operator fun invoke(): Flow<List<Long>> {
        return repository.lessonNumbers
    }
}