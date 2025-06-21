package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.core.ui.util.ApiKanjiResource
import fr.steph.kanji.core.ui.util.FuriganaResource
import fr.steph.kanji.feature_dictionary.domain.use_case.GetFurigana
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetStrokeFilenamesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LexemeDetailsViewModel(
    private val getKanjiInfo: GetKanjiInfoUseCase,
    private val getStrokeFilenames: GetStrokeFilenamesUseCase,
    private val getFurigana: GetFurigana
) : ViewModel() {

    private val apiResponseChannel = Channel<ApiKanjiResource>()
    val apiResponse = apiResponseChannel.receiveAsFlow()

    private val furiganaResponseChannel = Channel<FuriganaResource>()
    val furiganaResponse = furiganaResponseChannel.receiveAsFlow()

    fun fetchFurigana(context: Context, text: String) = viewModelScope.launch {
        val result = getFurigana(context, text)
        furiganaResponseChannel.send(result)
    }

    fun fetchKanji(context: Context, characters: String) = viewModelScope.launch {
        val result = getKanjiInfo(context, characters)
        apiResponseChannel.send(result)
    }

    fun getCharactersStrokeFilenames(characters: String) = getStrokeFilenames(characters)
}