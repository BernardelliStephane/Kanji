package fr.steph.kanji.core.data.repository

import fr.steph.kanji.R
import fr.steph.kanji.core.data.api.RetrofitInstance
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.data.util.safeApiCall
import fr.steph.kanji.core.ui.util.ApiKanjiResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.WordResource
import fr.steph.kanji.core.util.extension.isKanji

class ApiKanjiRepository {

    suspend fun getKanjiInfo(character: String): ApiKanjiResource =
        safeApiCall(
            apiCall = { RetrofitInstance.api.getKanjiInfo(character) },
            onSuccess = { kanjiInfo -> Resource.Success(kanjiInfo) }
        )

    suspend fun getCompoundInfo(characters: String): WordResource {
        val kanji = characters.find { it.isKanji() }
            ?: return Resource.Failure(R.string.no_kanji_found)

        return safeApiCall(
            apiCall = { RetrofitInstance.api.getWords(kanji.toString()) },
            onSuccess = { words -> findWordInResponse(words, characters) }
        )
    }

    private fun findWordInResponse(response: List<Word>, characters: String): WordResource {
        val matchingWords = response.filter { word ->
            word.variants.any { variant ->
                variant.written == characters
            }
        }

        if (matchingWords.isEmpty())
            return Resource.Failure(R.string.no_matching_word)

        return Resource.Success(matchingWords)
    }
}