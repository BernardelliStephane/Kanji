package fr.steph.kanji.ui.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.ApiKanji
import fr.steph.kanji.data.repository.ApiKanjiRepository
import fr.steph.kanji.data.repository.LessonRepository
import fr.steph.kanji.data.repository.LexemeRepository
import fr.steph.kanji.util.ConnectivityChecker.isNetworkAvailable
import fr.steph.kanji.util.extension.log
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class ApiLexemeViewModel(
    lessonRepo: LessonRepository,
    lexemeRepo: LexemeRepository,
    private val apiRepo: ApiKanjiRepository,
) : LexemeViewModel(lessonRepo, lexemeRepo) {

    private val _lastKanjiFetch: MutableLiveData<Resource?> = MutableLiveData()
    val lastKanjiFetch: LiveData<Resource?> = _lastKanjiFetch

    fun getKanjiInfo(character: String) = viewModelScope.launch {
        log("Search: $character")
        _lastKanjiFetch.postValue(Resource.Loading)
        try {
            if (isNetworkAvailable()) {
                val response = apiRepo.getKanjiInfo(character)
                _lastKanjiFetch.postValue(handleResponse(response))
            } else _lastKanjiFetch.postValue(Resource.Error(R.string.network_unavailable))
        } catch (t: Throwable) {
            when (t) {
                is SocketTimeoutException -> _lastKanjiFetch.postValue(Resource.Error(R.string.connexion_timeout))
                is IOException -> _lastKanjiFetch.postValue(Resource.Error(R.string.network_failure))
                else -> _lastKanjiFetch.postValue(Resource.Error(R.string.conversion_error))
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