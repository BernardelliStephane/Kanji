package fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.core.ui.util.LessonResource
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonNumbersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLessonUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddLessonViewModel(
    getLessonNumbers: GetLessonNumbersUseCase,
    private val insertLesson: InsertLessonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLessonState())
    val uiState = _uiState.asStateFlow()

    val lessonNumbers = getLessonNumbers().asLiveData()

    private val validationEventChannel = Channel<LessonResource>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: AddLessonEvent) {
        return when(event) {
            is AddLessonEvent.NumberChanged ->
                _uiState.update { it.copy(number = event.number) }

            is AddLessonEvent.LabelChanged ->
                _uiState.update { it.copy(label = event.label) }
        }
    }

    fun submitData() = viewModelScope.launch {
        _uiState.update { it.copy(isSubmitting = true) }

        val result = insertLesson(uiState.value, lessonNumbers.value ?: emptyList())

        result.insertionResult?.let {
            validationEventChannel.send(it)
        } ?: _uiState.update { currentUiState ->
            currentUiState.copy(
                numberErrorRes = result.numberErrorRes,
                labelErrorRes = result.labelErrorRes
            )
        }

        _uiState.update { it.copy(isSubmitting = false) }
    }
}