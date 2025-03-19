package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.R
import fr.steph.kanji.data.model.ApiKanji
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.viewmodel.AddLexemeViewModel.Resource
import fr.steph.kanji.util.ConnectivityChecker.isNetworkAvailable
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class GetKanjiInfoUseCase(private val repository: ApiKanjiRepository) {

    suspend operator fun invoke(character: String): Resource {
        try {
            if (!isNetworkAvailable())
                return Resource.Error(R.string.network_unavailable)

            val response = repository.getKanjiInfo(character)
            return handleResponse(response)

        } catch (t: Throwable) {
            return when (t) {
                is SocketTimeoutException -> Resource.Error(R.string.connexion_timeout)
                is IOException -> Resource.Error(R.string.network_failure)
                else -> Resource.Error(R.string.conversion_error)
            }
        }
    }

    private fun handleResponse(response: Response<ApiKanji>): Resource {
        return if (response.isSuccessful && response.body() != null)
            Resource.Success(response.body()!!)
        else Resource.Error(R.string.network_failure)
    }
}