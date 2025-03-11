package fr.steph.kanji.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.DialogFilterLexemesBinding
import fr.steph.kanji.ui.adapter.LessonAdapter
import fr.steph.kanji.ui.utils.StretchEdgeEffectFactory
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.ui.utils.recyclerview_selection.ItemKeyProvider
import fr.steph.kanji.ui.utils.recyclerview_selection.LessonDetailsLookup
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.LessonViewModel
import fr.steph.kanji.utils.extension.measureLayoutHeight

const val FILTER_LEXEMES_DIALOG_TAG = "filter_lexemes_dialog"
const val MAX_VISIBLE_ITEMS = 8

class FilterLexemesDialogFragment : DialogFragment(R.layout.dialog_filter_lexemes) {
    private var binding: DialogFilterLexemesBinding by autoCleared()

    private val viewModel: LessonViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            LessonViewModel(app.lessonRepository)
        }
    }

    private lateinit var lessonAdapter: LessonAdapter

    private lateinit var initialSelection: LongArray
    private lateinit var tracker: SelectionTracker<Long>

    private var successCallback: ((List<Long>) -> Unit?)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogFilterLexemesBinding.bind(view)

        initialSelection = requireArguments().getLongArray(ARG_INITIAL_SELECTION)!!

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        setupObservers()
        setupUI()
        setupTracker()
        setupListeners()
    }

    private fun setupUI() {
        lessonAdapter = LessonAdapter()

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
            successCallback?.invoke(tracker.selection.toList())
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
                    binding.selectAllCheckbox.isChecked = selectionSize == 0
                }
            })

        lessonAdapter.tracker = tracker

        if (initialSelection.isNotEmpty())
            tracker.setItemsSelected(initialSelection.toList(), true)
    }

    private fun setupObservers() {
        viewModel.allLessons.observe(viewLifecycleOwner) {
            if (it.size > MAX_VISIBLE_ITEMS) {
                binding.lessonRecyclerView.run {
                    val itemHeight = measureLayoutHeight(layoutInflater, R.layout.item_lesson)
                    layoutParams.height = itemHeight * MAX_VISIBLE_ITEMS
                }
            }

            lessonAdapter.updateLessons(it)
        }
    }

    fun setSuccessCallback(callback: (List<Long>) -> Unit): FilterLexemesDialogFragment {
        successCallback = callback
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