package fr.steph.kanji.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder
import fr.steph.kanji.databinding.DialogSortLexemesBinding
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.utils.extension.getSerializable

const val SORT_LEXEMES_DIALOG_TAG = "sort_lexemes_dialog"

class SortLexemesDialogFragment : DialogFragment(R.layout.dialog_sort_lexemes) {

    private var binding: DialogSortLexemesBinding by autoCleared()
    private var confirmCallback: ((SortField, SortOrder) -> Unit?)? = null
    private lateinit var sortField: SortField
    private lateinit var sortOrder: SortOrder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSortLexemesBinding.bind(view)

        sortField = requireArguments().getSerializable<SortField>(ARG_DEFAULT_FIELD)!!
        sortOrder = requireArguments().getSerializable<SortOrder>(ARG_DEFAULT_ORDER)!!

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() = with(binding) {
        when (sortField) {
            SortField.ID -> radioCreationDate.isChecked = true
            SortField.ROMAJI -> radioRomaji.isChecked = true
            SortField.MEANING -> radioMeaning.isChecked = true
        }

        when (sortOrder) {
            SortOrder.ASCENDING -> radioAscending.isChecked = true
            SortOrder.DESCENDING -> radioDescending.isChecked = true
        }
    }

    private fun setupListeners() = with(binding) {
        dialogCancelButton.setOnClickListener { dismiss() }
        dialogDoneButton.setOnClickListener {
            val sortField = when (sortBySelectionRadio.checkedRadioButtonId) {
                R.id.radio_meaning -> SortField.MEANING
                R.id.radio_romaji -> SortField.ROMAJI
                else -> SortField.ID
            }

            val sortOrder = when (directionSelectionRadio.checkedRadioButtonId) {
                R.id.radio_ascending -> SortOrder.ASCENDING
                else -> SortOrder.DESCENDING
            }

            confirmCallback?.invoke(sortField, sortOrder)
            dismiss()
        }
    }

    fun setConfirmCallback(callback: (SortField, SortOrder) -> Unit): SortLexemesDialogFragment {
        confirmCallback = callback
        return this
    }

    companion object {
        private const val ARG_DEFAULT_FIELD = "sort_field"
        private const val ARG_DEFAULT_ORDER = "sort_order"

        fun newInstance(sortField: SortField, sortOrder: SortOrder): SortLexemesDialogFragment {
            return SortLexemesDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DEFAULT_FIELD, sortField)
                    putSerializable(ARG_DEFAULT_ORDER, sortOrder)
                }
            }
        }
    }
}