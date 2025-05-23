package fr.steph.kanji.feature_dictionary.ui.dictionary.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.enumeration.SortField
import fr.steph.kanji.core.domain.enumeration.SortOrder
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.util.extension.getSerializable
import fr.steph.kanji.core.util.extension.setupDialogWindow
import fr.steph.kanji.databinding.DialogSortLexemesBinding

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

        setupDialogWindow()
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