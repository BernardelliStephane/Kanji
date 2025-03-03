package fr.steph.kanji.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.databinding.DialogFilterLexemesBinding
import fr.steph.kanji.ui.adapter.LessonAdapter
import fr.steph.kanji.ui.utils.StretchEdgeEffectFactory
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.ui.utils.recyclerview_selection.LexemeDetailsLookup
import fr.steph.kanji.ui.utils.recyclerview_selection.ItemKeyProvider
import fr.steph.kanji.ui.utils.recyclerview_selection.LessonDetailsLookup

const val FILTER_LEXEMES_DIALOG_TAG = "filter_lexemes_dialog"

class FilterLexemesDialogFragment : DialogFragment(R.layout.dialog_filter_lexemes) {
    private var binding: DialogFilterLexemesBinding by autoCleared()
    private lateinit var initialSelection: LongArray
    private lateinit var tracker: SelectionTracker<Long>

    private val items = List(100) { Lesson(it + 1L, "Label ${it + 1}") }
    private var lessonAdapter = LessonAdapter(items)
    private var confirmCallback: ((List<Long>) -> Unit?)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogFilterLexemesBinding.bind(view)

        initialSelection = requireArguments().getLongArray(ARG_INITIAL_SELECTION)!!

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        setupUI()
        setupTracker()
        setupListeners()
    }

    private fun setupUI() {
        binding.lessonRecyclerView.apply {
            adapter = lessonAdapter
            edgeEffectFactory = StretchEdgeEffectFactory()
        }

        if (lessons.isEmpty()) {
            //TODO Check "all"
        }
        else {
            //TODO Check all in lessons
        }
    }

    private fun setupListeners() = with(binding) {
        selectAllCheckbox.setOnClickListener {
            if (!selectAllCheckbox.isChecked) {
                /*TODO val items = viewModel.lexemes.value!!.map { it.id }
                tracker.setItemsSelected(items, true)*/
            }
        }

        dialogCancelButton.setOnClickListener { dismiss() }
        dialogDoneButton.setOnClickListener {
            confirmCallback?.invoke(tracker.selection.toList())
            dismiss()
        }
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "lexeme_selection", binding.lessonRecyclerView,
            ItemKeyProvider(binding.lessonRecyclerView), LessonDetailsLookup(binding.lessonRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    /*val selectionSize = tracker.selection.size()
                    viewModel.onSelectionChanged(selectionSize)*/
                }
            })

        lessonAdapter.tracker = tracker
    }

    fun setConfirmCallback(callback: (List<Long>) -> Unit): FilterLexemesDialogFragment {
        confirmCallback = callback
        return this
    }

    companion object {
        private const val ARG_INITIAL_SELECTION = "initial_lessons"

        fun newInstance(lessons: LongArray): FilterLexemesDialogFragment {
            return FilterLexemesDialogFragment().apply {
                arguments = Bundle().apply {
                    putLongArray(ARG_INITIAL_SELECTION, lessons)
                }
            }
        }
    }
}