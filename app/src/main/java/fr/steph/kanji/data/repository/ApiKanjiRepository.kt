package fr.steph.kanji.data.repository

import fr.steph.kanji.data.api.RetrofitInstance

class ApiKanjiRepository {

    suspend fun getKanjiInfo(character: String) =
        RetrofitInstance.api.getKanjiInfo(character)
}