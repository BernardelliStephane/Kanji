package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.data.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilterLexemesViewModel(repo: LessonRepository) : ViewModel() {
    val allLessons: LiveData<List<Lesson>> = repo.allLessons().asLiveData()
}