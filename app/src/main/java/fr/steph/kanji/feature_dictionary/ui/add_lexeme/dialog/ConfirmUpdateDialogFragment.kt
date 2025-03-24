package fr.steph.kanji.feature_dictionary.ui.add_lexeme.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.util.extension.setupDialogWindow
import fr.steph.kanji.databinding.DialogConfirmLexemeUpdateBinding

class ConfirmLexemeUpdateDialogFragment : DialogFragment(R.layout.dialog_confirm_lexeme_update) {

    private var binding: DialogConfirmLexemeUpdateBinding by autoCleared()
    private var successCallback: (() -> Unit?)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogConfirmLexemeUpdateBinding.bind(view)

        setupDialogWindow()

        binding.dialogCancelButton.setOnClickListener { dismiss() }
        binding.dialogDeleteButton.setOnClickListener { successCallback?.invoke(); dismiss() }
    }

    fun setSuccessCallback(callback: () -> Unit): ConfirmLexemeUpdateDialogFragment {
        successCallback = callback
        return this
    }
}