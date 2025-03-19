package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.R
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.domain.model.Lesson
import fr.steph.kanji.ui.core.model.LessonInsertResult
import fr.steph.kanji.ui.core.viewmodel.INSERTION_FAILURE
import fr.steph.kanji.ui.core.viewmodel.LessonViewModel.ValidationEvent
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.uistate.AddLessonFormState
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.util.validation.ValidateLessonField
import kotlinx.coroutines.flow.update

class InsertLessonUseCase(private val repository: LessonRepository) {

    suspend operator fun invoke(
        uiState: AddLessonFormState,
        lessonNumbers: List<Long>
    ): LessonInsertResult {

        val numberResult = ValidateLessonField.validateNumber(uiState.number, lessonNumbers)
        val labelResult = ValidateLessonField.validateLabel(uiState.label)

        val hasError = listOf(numberResult, labelResult).any { !it.successful }

        if (hasError)
            return LessonInsertResult(numberResult.errorMessageRes, labelResult.errorMessageRes)

        val lesson = Lesson(uiState.number.toLong(), uiState.label)

        repository.insertLesson(lesson).let {
            return if (it == INSERTION_FAILURE)
                LessonInsertResult(insertionResult = ValidationEvent.Failure(R.string.room_failure))
            else LessonInsertResult(insertionResult = ValidationEvent.Success(lesson))
        }
    }
}