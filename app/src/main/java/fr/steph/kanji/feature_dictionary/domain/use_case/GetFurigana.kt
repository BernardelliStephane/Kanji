package fr.steph.kanji.feature_dictionary.domain.use_case

import android.content.Context
import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.OracleRepository
import fr.steph.kanji.core.data.util.ConnectivityChecker.isNetworkAvailable
import fr.steph.kanji.core.ui.util.FuriganaResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.util.extension.isOnlyJapaneseCharacters

class GetFurigana(private val repository: OracleRepository) {

    suspend operator fun invoke(context: Context, text: String): FuriganaResource {
        if (!isNetworkAvailable(context))
            return Resource.Failure(R.string.network_unavailable)

        if (!text.isOnlyJapaneseCharacters())
            return Resource.Failure(R.string.format_failure)

        return repository.getFurigana(text)
    }
}