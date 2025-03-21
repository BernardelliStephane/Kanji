package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LessonRepository
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.INSERTION_FAILURE
import fr.steph.kanji.feature_dictionary.domain.model.LessonInsertResult
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonState
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.validation.ValidateLesson

class InsertLessonUseCase(private val repository: LessonRepository) {

    suspend operator fun invoke(
        uiState: AddLessonState,
        lessonNumbers: List<Long>
    ): LessonInsertResult {

        val numberResult = ValidateLesson.validateNumber(uiState.number, lessonNumbers)
        val labelResult = ValidateLesson.validateLabel(uiState.label)

        val hasError = listOf(numberResult, labelResult).any { !it.successful }

        if (hasError)
            return LessonInsertResult(numberResult.errorMessageRes, labelResult.errorMessageRes)

        val lesson = Lesson(uiState.number.toLong(), uiState.label)

        repository.insertLesson(lesson).let {
            return if (it == INSERTION_FAILURE)
                LessonInsertResult(insertionResult = Resource.Failure(R.string.room_failure))
            else LessonInsertResult(insertionResult = Resource.Success(lesson))
        }
    }
}