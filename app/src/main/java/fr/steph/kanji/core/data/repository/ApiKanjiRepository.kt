package fr.steph.kanji.core.data.repository

import fr.steph.kanji.core.data.api.RetrofitInstance

class ApiKanjiRepository {

    suspend fun getKanjiInfo(character: String) =
        RetrofitInstance.api.getKanjiInfo(character)
}