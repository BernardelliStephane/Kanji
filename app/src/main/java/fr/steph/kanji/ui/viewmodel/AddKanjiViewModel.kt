package fr.steph.kanji.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.data.repository.KanjiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

const val FAILURE = -1L

class AddKanjiViewModel(private val repo: KanjiRepository) : ViewModel() {

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    var id = 0
    var character: String = ""
    var kana: String = ""
    var romaji: String = ""
    var translation: String = ""

    fun performValidation() {
        // TODO Verify entries before upserting kanji
        val kanji = Kanji(id, character, kana, romaji, translation)
        upsertKanji(kanji)
    }

    private fun upsertKanji(kanji: Kanji) = viewModelScope.launch {
        repo.upsertKanji(kanji).let {
            if(it == FAILURE)
                validationEventChannel.send(ValidationEvent.Failure(R.string.room_insertion_failure))
            else validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent {
        data class Failure (val failureMessage: Int): ValidationEvent()
        data object Success: ValidationEvent()
    }
}