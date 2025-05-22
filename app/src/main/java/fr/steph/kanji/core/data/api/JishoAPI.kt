package fr.steph.kanji.core.data.api

import fr.steph.kanji.core.data.model.jisho.JishoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JishoAPI {

    companion object {
        const val JISHO_BASE_URL = "https://jisho.org/api/v1/"
    }

    @GET("search/words")
    suspend fun searchWord(
        @Query("keyword") keyword: String
    ): Response<JishoResponse>
}