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
import fr.steph.kanji.ui.utils.recyclerview_selection.ItemKeyProvider
import fr.steph.kanji.ui.utils.recyclerview_selection.LessonDetailsLookup

const val FILTER_LEXEMES_DIALOG_TAG = "filter_lexemes_dialog"

class FilterLexemesDialogFragment : DialogFragment(R.layout.dialog_filter_lexemes) {
    private var binding: DialogFilterLexemesBinding by autoCleared()

    private lateinit var lessonAdapter: LessonAdapter

    private lateinit var initialSelection: LongArray
    private lateinit var tracker: SelectionTracker<Long>

    private var confirmCallback: ((List<Long>) -> Unit?)? = null

    private val items = List(100) { Lesson(it + 1L, "Lesson ${it + 1}") }

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
        //TODO Items = viewModel.lessons
        lessonAdapter = LessonAdapter(items)

        binding.lessonRecyclerView.apply {
            adapter = lessonAdapter
            edgeEffectFactory = StretchEdgeEffectFactory()
        }

        if (initialSelection.isEmpty())
            binding.selectAllCheckbox.isChecked = true
    }

    private fun setupListeners() = with(binding) {
        selectAllLayout.setOnClickListener {
            if (tracker.selection.size() != 0)
                tracker.clearSelection()
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
                    val selectionSize = tracker.selection.size()
                    binding.selectAllCheckbox.isChecked = selectionSize == 0 || selectionSize == items.size
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