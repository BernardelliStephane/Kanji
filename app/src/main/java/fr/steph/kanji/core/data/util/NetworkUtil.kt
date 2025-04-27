package fr.steph.kanji.core.data.util

import fr.steph.kanji.R
import fr.steph.kanji.core.ui.util.Resource
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T : Any> safeApiCall(
    apiCall: suspend () -> Response<T>,
    onSuccess: (T) -> Resource<T>
): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null)
            onSuccess(response.body()!!)
        else
            Resource.Failure(R.string.network_failure)
    } catch (t: Throwable) {
        when (t) {
            is SocketTimeoutException -> Resource.Failure(R.string.connexion_timeout)
            is IOException -> Resource.Failure(R.string.network_failure)
            else -> Resource.Failure(R.string.conversion_error)
        }
    }
}