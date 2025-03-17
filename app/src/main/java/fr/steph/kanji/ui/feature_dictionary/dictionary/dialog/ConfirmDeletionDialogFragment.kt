package fr.steph.kanji.ui.feature_dictionary.dictionary.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.databinding.DialogConfirmDeletionBinding
import fr.steph.kanji.ui.core.util.autoCleared

const val DELETE_DIALOG_TAG = "confirm_deletion_dialog"

class ConfirmDeletionDialogFragment : DialogFragment(R.layout.dialog_confirm_deletion) {

    private var binding: DialogConfirmDeletionBinding by autoCleared()
    private var successCallback: (() -> Unit?)? = null
    private var selectionSize = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogConfirmDeletionBinding.bind(view)

        selectionSize = requireArguments().getInt(ARG_SELECTION_SIZE, 0)

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        binding.dialogText.text = resources.getQuantityString(R.plurals.item_deletion_count, selectionSize, selectionSize)
        binding.dialogCancelButton.setOnClickListener { dismiss() }
        binding.dialogDeleteButton.setOnClickListener { successCallback?.invoke(); dismiss() }
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