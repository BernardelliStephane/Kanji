package fr.steph.kanji

import android.app.Application
import fr.steph.kanji.core.data.LexemeDatabase
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.core.data.repository.LessonRepository
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.feature_dictionary.domain.use_case.AddLexemeUseCases
import fr.steph.kanji.feature_dictionary.domain.use_case.DeleteLexemesFromSelectionUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.DictionaryUseCases
import fr.steph.kanji.feature_dictionary.domain.use_case.FilterLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonsUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLexemeByCharactersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLessonUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchInFilteredLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.UpsertLexemeUseCase

class KanjiApplication: Application() {
    private val database by lazy { LexemeDatabase.getDatabase(this) }
    private val apiRepository by lazy { ApiKanjiRepository() }
    private val lexemeRepository by lazy { LexemeRepository(database.lexemeDao()) }
    val lessonRepository by lazy { LessonRepository(database.lessonDao()) }

    val addLexemeUseCases by lazy {
        AddLexemeUseCases(
            GetKanjiInfoUseCase(apiRepository),
            UpsertLexemeUseCase(lexemeRepository),
            GetLexemeByCharactersUseCase(lexemeRepository),
            GetLessonsUseCase(lessonRepository),
            InsertLessonUseCase(lessonRepository)
        )
    }

    val dictionaryUseCases by lazy {
        DictionaryUseCases(
            GetLexemesUseCase(lexemeRepository),
            SearchLexemesUseCase(lexemeRepository),
            FilterLexemesUseCase(lexemeRepository),
            SearchInFilteredLexemesUseCase(lexemeRepository),
            DeleteLexemesFromSelectionUseCase(lexemeRepository),
            GetLessonsUseCase(lessonRepository)
        )
    }
}