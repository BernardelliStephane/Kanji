package fr.steph.kanji.core.ui.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * A generic [ViewModelProvider.Factory] helper function that creates the correct factory for a given [ViewModel].
 *
 * This function simplifies the creation of a [ViewModelProvider.Factory], which is used to instantiate a [ViewModel].
 */
fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}