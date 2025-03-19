package fr.steph.kanji.ui.feature_dictionary.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fr.steph.kanji.domain.model.Lesson
import fr.steph.kanji.ui.core.use_case.GetLessonsUseCase

class FilterLexemesViewModel(getLessons: GetLessonsUseCase) : ViewModel() {
    val allLessons = getLessons().asLiveData()

    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data class Success(val lesson: Lesson) : ValidationEvent()
    }
}