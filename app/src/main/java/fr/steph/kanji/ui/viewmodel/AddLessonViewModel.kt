package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.ui.uistate.AddLessonFormEvent
import fr.steph.kanji.ui.uistate.AddLessonFormState
import fr.steph.kanji.ui.utils.form_validation.ValidateLessonField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddLessonViewModel(repo: LessonRepository) : LessonViewModel(repo) {
    private val _uiState = MutableStateFlow(AddLessonFormState())
    val uiState = _uiState.asStateFlow()

    val lessonNumbers = MediatorLiveData<List<Long>>().apply{
        val observer = Observer<List<Lesson>> { value = it.map { lesson -> lesson.number } }
        addSource(allLessons, observer)
    }

    fun onEvent(event: AddLessonFormEvent) {
        return when(event) {
            is AddLessonFormEvent.NumberChanged ->
                _uiState.update { it.copy(number = event.number) }

            is AddLessonFormEvent.LabelChanged ->
                _uiState.update { it.copy(label = event.label) }

            is AddLessonFormEvent.InsertionFailure ->
                _uiState.update { it.copy(numberErrorRes = event.failureMessageRes) }
        }
    }

    fun submitData() {
        _uiState.update { it.copy(isSubmitting = true) }

        val lessonNumbers = lessonNumbers.value ?: emptyList()
        val numberResult = ValidateLessonField.validateNumber(uiState.value.number, lessonNumbers)
        val labelResult = ValidateLessonField.validateLabel(uiState.value.label)

        _uiState.update { currentUiState ->
            currentUiState.copy(
                numberErrorRes = numberResult.errorMessageRes,
                labelErrorRes = labelResult.errorMessageRes
            )
        }

        val hasError = listOf(numberResult, labelResult).any { !it.successful }

        if (hasError)
            return _uiState.update { it.copy(isSubmitting = false) }

        val lesson = Lesson(uiState.value.number.toLong(), uiState.value.label)

        insertLesson(lesson)
    }
}