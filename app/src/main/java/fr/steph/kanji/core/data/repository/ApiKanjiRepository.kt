package fr.steph.kanji.core.data.repository

import fr.steph.kanji.core.data.api.RetrofitInstance
import fr.steph.kanji.core.ui.util.ApiKanjiResource
import fr.steph.kanji.core.ui.util.Resource

class ApiKanjiRepository {

    suspend fun getKanjiInfo(character: String): ApiKanjiResource =
        safeApiCall(
            apiCall = { RetrofitInstance.api.getKanjiInfo(character) },
            onSuccess = { kanjiInfo -> Resource.Success(kanjiInfo) }
        )
}