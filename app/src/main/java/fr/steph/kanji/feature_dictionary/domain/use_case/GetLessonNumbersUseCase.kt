package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LessonRepository
import kotlinx.coroutines.flow.Flow

class GetLessonNumbersUseCase(private val repository: LessonRepository) {

    operator fun invoke(): Flow<List<Long>> {
        return repository.lessonNumbers
    }
}