package fr.steph.kanji.core.data.repository

import fr.steph.kanji.core.data.api.RetrofitInstance
import fr.steph.kanji.core.data.model.oracle.FuriganaRequest
import fr.steph.kanji.core.data.util.safeApiCall
import fr.steph.kanji.core.ui.util.FuriganaResource
import fr.steph.kanji.core.ui.util.Resource

class OracleRepository {

    suspend fun getFurigana(text: String): FuriganaResource {
        return safeApiCall(
            apiCall = { RetrofitInstance.oracleApi.getFurigana(FuriganaRequest(text)) },
            onSuccess = { furigana -> Resource.Success(furigana) }
        )
    }
}