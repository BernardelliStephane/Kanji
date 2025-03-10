package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.data.repository.LessonRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

open class LessonViewModel(private val repo: LessonRepository) : ViewModel() {
    val allLessons = repo.allLessons().asLiveData()

    private val lessonValidationEventChannel = Channel<ValidationEvent>()
    val lessonValidationEvents = lessonValidationEventChannel.receiveAsFlow()

    fun insertLesson(lesson: Lesson) = viewModelScope.launch {
        repo.insertLesson(lesson).let {
            if (it == FAILURE)
                lessonValidationEventChannel.send(ValidationEvent.Failure(R.string.room_insertion_failure))
            else lessonValidationEventChannel.send(ValidationEvent.Success(lesson))
        }
    }

    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data class Success(val lesson: Lesson) : ValidationEvent()
    }
}