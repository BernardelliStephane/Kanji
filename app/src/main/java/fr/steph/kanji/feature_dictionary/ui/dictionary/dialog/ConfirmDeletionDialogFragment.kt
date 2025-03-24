package fr.steph.kanji.feature_dictionary.ui.dictionary.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.databinding.DialogConfirmDeletionBinding
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.util.extension.setupDialogWindow

class ConfirmDeletionDialogFragment : DialogFragment(R.layout.dialog_confirm_deletion) {

    private var binding: DialogConfirmDeletionBinding by autoCleared()
    private var successCallback: (() -> Unit?)? = null
    private var selectionSize = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogConfirmDeletionBinding.bind(view)

        selectionSize = requireArguments().getInt(ARG_SELECTION_SIZE, 0)

        setupDialogWindow()
        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        dialogText.text = resources.getQuantityString(R.plurals.item_deletion_count, selectionSize, selectionSize)
        dialogCancelButton.setOnClickListener { dismiss() }
        dialogDeleteButton.setOnClickListener { successCallback?.invoke(); dismiss() }
    }

    fun setSuccessCallback(callback: () -> Unit): ConfirmDeletionDialogFragment {
        successCallback = callback
        return this
    }

    companion object {
        private const val ARG_SELECTION_SIZE = "selection_size"

        fun newInstance(selectionSize: Int): ConfirmDeletionDialogFragment {
            return ConfirmDeletionDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SELECTION_SIZE, selectionSize)
                }
            }
        }
    }
}