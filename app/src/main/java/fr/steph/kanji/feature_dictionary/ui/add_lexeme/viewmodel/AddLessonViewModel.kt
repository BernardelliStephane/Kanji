package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonNumbersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLessonUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonFormEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonFormState
import fr.steph.kanji.core.ui.viewmodel.FormViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLessonViewModel(
    getLessonNumbers: GetLessonNumbersUseCase,
    private val insertLesson: InsertLessonUseCase
) : FormViewModel() {

    private val _uiState = MutableStateFlow(AddLessonFormState())
    val uiState = _uiState.asStateFlow()

    val lessonNumbers = getLessonNumbers().asLiveData()

    fun onEvent(event: AddLessonFormEvent) {
        return when(event) {
            is AddLessonFormEvent.NumberChanged ->
                _uiState.update { it.copy(number = event.number) }

            is AddLessonFormEvent.LabelChanged ->
                _uiState.update { it.copy(label = event.label) }
        }
    }

    fun submitData() = viewModelScope.launch {
        _uiState.update { it.copy(isSubmitting = true) }

        val result = insertLesson(uiState.value, lessonNumbers.value ?: emptyList())

        result.insertionResult?.let {
            sendValidationEvent(it)
        } ?: _uiState.update { currentUiState -> currentUiState.copy(
            numberErrorRes = result.numberErrorRes,
            labelErrorRes = result.labelErrorRes
        )}

        _uiState.update { it.copy(isSubmitting = false) }
    }
}