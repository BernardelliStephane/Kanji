package fr.steph.kanji.core.data.api

import fr.steph.kanji.core.data.api.JishoAPI.Companion.JISHO_BASE_URL
import fr.steph.kanji.core.data.api.KanjiAPI.Companion.KANJI_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {

        private val logging by lazy {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            interceptor
        }

        private val client by lazy {
            OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
        }

        private val kanjiApiRetrofit by lazy {
            Retrofit.Builder()
                .baseUrl(KANJI_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        private val jishoRetrofit by lazy {
            Retrofit.Builder()
                .baseUrl(JISHO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val kanjiApi: KanjiAPI by lazy {
            kanjiApiRetrofit.create(KanjiAPI::class.java)
        }

        val jishoApi: JishoAPI by lazy {
            jishoRetrofit.create(JishoAPI::class.java)
        }
    }
}