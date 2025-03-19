package fr.steph.kanji.ui.core.viewmodel

import androidx.lifecycle.ViewModel
import fr.steph.kanji.ui.feature_dictionary.dictionary.viewmodel.FilterLexemesViewModel.ValidationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class FormViewModel: ViewModel() {
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    protected suspend fun sendValidationEvent(event: ValidationEvent) =
        validationEventChannel.send(event)

}