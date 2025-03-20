package fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonsUseCase

class FilterLexemesViewModel(getLessons: GetLessonsUseCase) : ViewModel() {
    val allLessons = getLessons().asLiveData()

    sealed class ValidationEvent {
        data class Failure(val failureMessage: Int) : ValidationEvent()
        data class Success(val lesson: Lesson) : ValidationEvent()
    }
}