package fr.steph.kanji.core.ui.util

import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme

typealias LessonResource = Resource<Lesson>
typealias LexemeResource = Resource<Lexeme>
typealias ApiKanjiResource = Resource<ApiKanji>
typealias WordResource = Resource<List<Word>>

sealed class Resource<T> {
    data class Failure<T>(val failureMessage: Int) : Resource<T>()
    data class Success<T>(val data: T? = null) : Resource<T>()
}