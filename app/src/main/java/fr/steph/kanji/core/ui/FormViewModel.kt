package fr.steph.kanji.core.ui

import androidx.lifecycle.ViewModel
import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.FilterLexemesViewModel.ValidationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class FormViewModel: ViewModel() {
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    protected suspend fun sendValidationEvent(event: ValidationEvent) =
        validationEventChannel.send(event)

}