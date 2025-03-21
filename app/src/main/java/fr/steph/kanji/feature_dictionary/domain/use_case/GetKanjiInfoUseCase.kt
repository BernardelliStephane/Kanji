package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.data.repository.ApiKanjiRepository
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.AddLexemeViewModel.ApiResource
import fr.steph.kanji.core.util.ConnectivityChecker.isNetworkAvailable
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class GetKanjiInfoUseCase(private val repository: ApiKanjiRepository) {

    suspend operator fun invoke(character: String): ApiResource {
        try {
            if (!isNetworkAvailable())
                return ApiResource.Error(R.string.network_unavailable)

            val response = repository.getKanjiInfo(character)
            return handleResponse(response)

        } catch (t: Throwable) {
            return when (t) {
                is SocketTimeoutException -> ApiResource.Error(R.string.connexion_timeout)
                is IOException -> ApiResource.Error(R.string.network_failure)
                else -> ApiResource.Error(R.string.conversion_error)
            }
        }
    }

    private fun handleResponse(response: Response<ApiKanji>): ApiResource {
        return if (response.isSuccessful && response.body() != null)
            ApiResource.Success(response.body()!!)
        else ApiResource.Error(R.string.network_failure)
    }
}