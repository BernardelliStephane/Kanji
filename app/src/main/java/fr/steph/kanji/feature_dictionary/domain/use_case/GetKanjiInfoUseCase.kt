package fr.steph.kanji.feature_dictionary.domain.use_case

import android.content.Context
import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.core.data.util.ConnectivityChecker.isNetworkAvailable
import fr.steph.kanji.core.ui.util.ApiKanjiResource
import fr.steph.kanji.core.ui.util.Resource

class GetKanjiInfoUseCase(private val repository: ApiKanjiRepository) {

    suspend operator fun invoke(context: Context, character: String): ApiKanjiResource {
        if (!isNetworkAvailable(context))
            return Resource.Failure(R.string.network_unavailable)

        return repository.getKanjiInfo(character)
    }
}