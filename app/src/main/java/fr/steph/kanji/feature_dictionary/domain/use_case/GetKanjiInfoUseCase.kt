package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.core.ui.util.ApiResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.data.util.ConnectivityChecker.isNetworkAvailable
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class GetKanjiInfoUseCase(private val repository: ApiKanjiRepository) {

    suspend operator fun invoke(character: String): ApiResource {
        try {
            if (!isNetworkAvailable())
                return Resource.Failure(R.string.network_unavailable)

            val response = repository.getKanjiInfo(character)
            return handleResponse(response)

        } catch (t: Throwable) {
            return when (t) {
                is SocketTimeoutException -> Resource.Failure(R.string.connexion_timeout)
                is IOException -> Resource.Failure(R.string.network_failure)
                else -> Resource.Failure(R.string.conversion_error)
            }
        }
    }

    private fun handleResponse(response: Response<ApiKanji>): ApiResource {
        return if (response.isSuccessful && response.body() != null)
            Resource.Success(response.body()!!)
        else Resource.Failure(R.string.network_failure)
    }
}