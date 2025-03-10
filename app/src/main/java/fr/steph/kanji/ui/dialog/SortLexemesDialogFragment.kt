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
    private var successCallback: ((SortField, SortOrder) -> Unit?)? = null
    private lateinit var sortField: SortField
    private lateinit var sortOrder: SortOrder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSortLexemesBinding.bind(view)

        sortField = requireArguments().getSerializable<SortField>(ARG_INITIAL_FIELD)!!
        sortOrder = requireArguments().getSerializable<SortOrder>(ARG_INITIAL_ORDER)!!

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
            SortField.MEANING -> radioMeaning.isChecked = true
            SortField.LESSON_NUMBER -> radioLessonNumber.isChecked = true
            SortField.ROMAJI -> radioRomaji.isChecked = true
            SortField.ID -> radioCreationDate.isChecked = true
        }

        when (sortOrder) {
            SortOrder.ASCENDING -> radioAscending.isChecked = true
            SortOrder.DESCENDING -> radioDescending.isChecked = true
        }
    }

    private fun setupListeners() = with(binding) {
        sortBySelectionRadio.setOnCheckedChangeListener { _, checkedId ->
            sortField = when(checkedId) {
                R.id.radio_meaning -> SortField.MEANING
                R.id.radio_romaji -> SortField.ROMAJI
                R.id.radio_lesson_number -> SortField.LESSON_NUMBER
                else -> SortField.ID
            }
        }

        directionSelectionRadio.setOnCheckedChangeListener { _, checkedId ->
            sortOrder = when(checkedId) {
                R.id.radio_ascending -> SortOrder.ASCENDING
                else -> SortOrder.DESCENDING
            }
        }

        dialogCancelButton.setOnClickListener { dismiss() }
        dialogDoneButton.setOnClickListener {
            successCallback?.invoke(sortField, sortOrder)
            dismiss()
        }
    }

    fun setSuccessCallback(callback: (SortField, SortOrder) -> Unit): SortLexemesDialogFragment {
        successCallback = callback
        return this
    }

    companion object {
        private const val ARG_INITIAL_FIELD = "initial_sort_field"
        private const val ARG_INITIAL_ORDER = "initial_sort_order"

        fun newInstance(sortField: SortField, sortOrder: SortOrder): SortLexemesDialogFragment {
            return SortLexemesDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_INITIAL_FIELD, sortField)
                    putSerializable(ARG_INITIAL_ORDER, sortOrder)
                }
            }
        }
    }
}