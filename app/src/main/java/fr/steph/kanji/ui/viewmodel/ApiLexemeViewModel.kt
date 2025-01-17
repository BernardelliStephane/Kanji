package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.ApiKanji
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.data.utils.ConnectivityChecker.isNetworkAvailable
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class ApiLexemeViewModel(
    repo: LexemeRepository,
    private val apiRepo: ApiKanjiRepository? = null,
) : LexemeViewModel(repo) {

    private val _apiKanji: MutableLiveData<Resource?> = MutableLiveData()
    val apiKanji: LiveData<Resource?> = _apiKanji

    fun getKanjiInfo(character: String) = viewModelScope.launch {
        _apiKanji.postValue(Resource.Loading)
        try {
            if (isNetworkAvailable()) {
                val response = apiRepo!!.getKanjiInfo(character)
                _apiKanji.postValue(handleResponse(response))
            } else _apiKanji.postValue(Resource.Error(R.string.network_unavailable))
        } catch (t: Throwable) {
            when (t) {
                is SocketTimeoutException -> _apiKanji.postValue(Resource.Error(R.string.connexion_timeout))
                is IOException -> _apiKanji.postValue(Resource.Error(R.string.network_failure))
                else -> _apiKanji.postValue(Resource.Error(R.string.conversion_error))
            }
        }
    }

    private fun handleResponse(response: Response<ApiKanji>): Resource {
        return if (response.isSuccessful && response.body() != null)
            Resource.Success(response.body()!!)
        else Resource.Error(R.string.network_failure)
    }

    sealed class Resource {
        data object Loading : Resource()
        data class Error(val message: Int) : Resource()
        data class Success(val data: ApiKanji) : Resource()
    }
}