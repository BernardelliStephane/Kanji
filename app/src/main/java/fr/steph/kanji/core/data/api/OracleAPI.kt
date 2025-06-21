package fr.steph.kanji.core.data.api

import fr.steph.kanji.core.data.model.oracle.FuriganaRequest
import fr.steph.kanji.core.data.model.oracle.FuriganaResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OracleAPI {

    companion object {
        const val ORACLE_BASE_URL = "https://kuroshiro-server.duckdns.org/"
    }

    @POST("furigana")
    suspend fun getFurigana(
        @Body request: FuriganaRequest
    ): Response<FuriganaResult>
}