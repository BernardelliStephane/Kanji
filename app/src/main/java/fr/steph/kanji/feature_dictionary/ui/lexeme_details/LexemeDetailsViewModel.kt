package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.core.ui.util.ApiResource
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LexemeDetailsViewModel(private val getKanjiInfo: GetKanjiInfoUseCase) : ViewModel() {

    private val apiResponseChannel = Channel<ApiResource>()
    val apiResponse = apiResponseChannel.receiveAsFlow()

    fun fetchKanji(characters: String) = viewModelScope.launch {
        val result = getKanjiInfo(characters)
        apiResponseChannel.send(result)
    }
}