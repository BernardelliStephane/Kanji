package fr.steph.kanji.core.ui.util

import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme

typealias LessonResource = Resource<Lesson>
typealias LexemeResource = Resource<Lexeme>

sealed class Resource<T> {
    data class Failure<T>(val failureMessage: Int) : Resource<T>()
    data class Success<T>(val data: T? = null) : Resource<T>()
}