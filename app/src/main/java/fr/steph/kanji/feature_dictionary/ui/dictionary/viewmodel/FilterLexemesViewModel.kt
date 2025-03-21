package fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonsUseCase

class FilterLexemesViewModel(getLessons: GetLessonsUseCase) : ViewModel() {
    val allLessons = getLessons().asLiveData()
}