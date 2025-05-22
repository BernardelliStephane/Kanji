package fr.steph.kanji.core.data.api

import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.model.word.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface KanjiAPI {

    companion object {
        const val KANJI_API_BASE_URL = "https://kanjiapi.dev/v1/"
    }

    @GET("kanji/{character}")
    suspend fun getKanjiInfo(
        @Path("character") character: String
    ): Response<ApiKanji>

    @GET("words/{character}")
    suspend fun getWords(
        @Path("character") character: String
    ): Response<List<Word>>
}