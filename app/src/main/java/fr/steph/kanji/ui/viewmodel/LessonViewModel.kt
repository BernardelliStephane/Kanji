package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.data.repository.LessonRepository
import kotlinx.coroutines.launch

open class LessonViewModel(private val repo: LessonRepository): ViewModel() {
    val allLessons: LiveData<List<Lesson>> = repo.allLessons().asLiveData()

    fun upsertLesson(lesson: Lesson) = viewModelScope.launch {
        repo.upsertLesson(lesson)
    }
}