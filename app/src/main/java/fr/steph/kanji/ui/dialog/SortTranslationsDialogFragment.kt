package fr.steph.kanji.ui.dialog

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.data.utils.enumeration.SortField
import fr.steph.kanji.data.utils.enumeration.SortOrder
import fr.steph.kanji.databinding.DialogSortTranslationsBinding
import fr.steph.kanji.ui.utils.autoCleared

const val SORT_TRANSLATIONS_DIALOG_TAG = "sort_translations_dialog"

class SortTranslationsDialogFragment : DialogFragment(R.layout.dialog_sort_translations) {

    private var binding: DialogSortTranslationsBinding by autoCleared()
    private var confirmCallback: ((SortField, SortOrder) -> Unit?)? = null
    private lateinit var sortField: SortField
    private lateinit var sortOrder: SortOrder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSortTranslationsBinding.bind(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sortField = requireArguments().getSerializable(ARG_DEFAULT_FIELD, SortField::class.java)!!
            sortOrder = requireArguments().getSerializable(ARG_DEFAULT_ORDER, SortOrder::class.java)!!
        }
        else {
            sortField = requireArguments().getSerializable(ARG_DEFAULT_FIELD) as SortField
            sortOrder = requireArguments().getSerializable(ARG_DEFAULT_ORDER) as SortOrder
        }

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        binding.apply {
            when (sortField) {
                SortField.ID -> radioCreationDate.isChecked = true
                SortField.ROMAJI -> radioRomaji.isChecked = true
                SortField.MEANING -> radioTranslation.isChecked = true
            }

            if (sortOrder == SortOrder.ASCENDING) radioAscending.isChecked = true
            else radioDescending.isChecked = true

            dialogCancelButton.setOnClickListener { dismiss() }
            dialogDoneButton.setOnClickListener {
                val sortField = when(sortBySelectionRadio.checkedRadioButtonId) {
                    R.id.radio_translation -> SortField.MEANING
                    R.id.radio_romaji -> SortField.ROMAJI
                    R.id.radio_creation_date -> SortField.ID
                    else -> SortField.ID
                }

                val sortOrder = when(directionSelectionRadio.checkedRadioButtonId) {
                    R.id.radio_ascending -> SortOrder.ASCENDING
                    R.id.radio_descending -> SortOrder.DESCENDING
                    else -> SortOrder.ASCENDING
                }

                confirmCallback?.invoke(sortField, sortOrder)
                dismiss()
            }
        }
    }

    fun setConfirmCallback(callback: (SortField, SortOrder) -> Unit): SortTranslationsDialogFragment {
        confirmCallback = callback
        return this
    }

    companion object {
        private const val ARG_DEFAULT_FIELD = "sort_field"
        private const val ARG_DEFAULT_ORDER = "sort_order"

        fun newInstance(sortField: SortField, sortOrder: SortOrder): SortTranslationsDialogFragment {
            return SortTranslationsDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DEFAULT_FIELD, sortField)
                    putSerializable(ARG_DEFAULT_ORDER, sortOrder)
                }
            }
        }
    }
}