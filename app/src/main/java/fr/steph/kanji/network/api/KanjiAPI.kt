package fr.steph.kanji.network.api

import fr.steph.kanji.network.model.ApiKanji
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface KanjiAPI {

    companion object {
        const val BASE_URL = "https://kanjiapi.dev/v1/"
    }

    @GET("kanji/{character}")
    suspend fun getKanjiInfo(
        @Path("character") character: String,
    ): Response<ApiKanji>
}