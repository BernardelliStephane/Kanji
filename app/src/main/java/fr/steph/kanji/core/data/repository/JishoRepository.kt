package fr.steph.kanji.core.data.repository

import fr.steph.kanji.R
import fr.steph.kanji.core.data.api.RetrofitInstance
import fr.steph.kanji.core.data.model.jisho.JishoResponse
import fr.steph.kanji.core.data.util.safeApiCall
import fr.steph.kanji.core.ui.util.JishoResource
import fr.steph.kanji.core.ui.util.Resource

class JishoRepository {

    suspend fun getCompoundInfo(characters: String): JishoResource {
        return safeApiCall(
            apiCall = { RetrofitInstance.jishoApi.searchWord(characters) },
            onSuccess = { jishoResponse -> filterTranslations(jishoResponse, characters) }
        )
    }

    private fun filterTranslations(response: JishoResponse, characters: String): JishoResource {
        val validTranslations = response.translations.filter { entry ->
            entry.writings.any { it.word == characters }
        }

        if (validTranslations.isEmpty())
            return Resource.Failure(R.string.no_matching_translation)

        return Resource.Success(JishoResponse(validTranslations))
    }
}