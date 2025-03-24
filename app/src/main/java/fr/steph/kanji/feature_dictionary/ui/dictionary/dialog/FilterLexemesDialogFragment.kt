package fr.steph.kanji.feature_dictionary.ui.dictionary.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.ui.util.ItemKeyProvider
import fr.steph.kanji.core.ui.util.StretchEdgeEffectFactory
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.extension.measureLayoutHeight
import fr.steph.kanji.core.util.extension.setupDialogWindow
import fr.steph.kanji.databinding.DialogFilterLexemesBinding
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonsUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter.LessonAdapter
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.LessonDetailsLookup
import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.FilterLexemesViewModel

const val MAX_VISIBLE_ITEMS = 8

class FilterLexemesDialogFragment : DialogFragment(R.layout.dialog_filter_lexemes) {
    private var binding: DialogFilterLexemesBinding by autoCleared()

    private val viewModel: FilterLexemesViewModel by viewModels {
        val repo = (activity?.application as KanjiApplication).lessonRepository
        viewModelFactory {
            FilterLexemesViewModel(GetLessonsUseCase(repo))
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

        setupDialogWindow()
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