package fr.steph.kanji.core.ui.util

import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.model.oracle.FuriganaResult
import fr.steph.kanji.core.data.model.jisho.JishoResponse
import fr.steph.kanji.core.domain.model.DatabaseModel
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.domain.model.Lexeme

typealias LessonResource = Resource<Lesson>
typealias LexemeResource = Resource<Lexeme>
typealias DatabaseResource = Resource<DatabaseModel>
typealias ApiKanjiResource = Resource<ApiKanji>
typealias JishoResource = Resource<JishoResponse>
typealias FuriganaResource = Resource<FuriganaResult>

sealed class Resource<T> {
    data class Failure<T>(val failureMessage: Int) : Resource<T>()
    data class Success<T>(val data: T? = null) : Resource<T>()
}