package fr.steph.kanji.feature_dictionary.domain.use_case

import android.content.Context
import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.core.data.util.ConnectivityChecker.isNetworkAvailable
import fr.steph.kanji.core.ui.util.JishoResource
import fr.steph.kanji.core.ui.util.Resource

class GetCompoundInfoUseCase(private val repository: ApiKanjiRepository) {

    suspend operator fun invoke(context: Context, characters: String): JishoResource {
        if (!isNetworkAvailable(context))
            return Resource.Failure(R.string.network_unavailable)

        return repository.getCompoundInfo(characters)
    }
}