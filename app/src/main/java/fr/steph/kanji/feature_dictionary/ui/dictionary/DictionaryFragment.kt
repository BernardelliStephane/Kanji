package fr.steph.kanji.feature_dictionary.ui.dictionary

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.feature_dictionary.domain.use_case.DeleteLexemesFromSelectionUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.FilterLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchInFilteredLexemesUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.SearchLexemesUseCase
import fr.steph.kanji.feature_dictionary.ui.dictionary.adapter.LexemeAdapter
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.ConfirmDeletionDialogFragment
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.FilterLexemesDialogFragment
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.SortLexemesDialogFragment
import fr.steph.kanji.core.ui.util.StretchEdgeEffectFactory
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.LexemeDetailsLookup
import fr.steph.kanji.core.ui.util.ItemKeyProvider
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.DictionaryViewModel
import fr.steph.kanji.core.ui.LexemeViewModel.ValidationEvent.Success
import fr.steph.kanji.core.ui.LexemeViewModel.ValidationEvent.Failure
import fr.steph.kanji.core.util.DELETE_DIALOG_TAG
import fr.steph.kanji.core.util.FILTER_LEXEMES_DIALOG_TAG
import fr.steph.kanji.core.util.SORT_LEXEMES_DIALOG_TAG
import fr.steph.kanji.core.util.extension.safeNavigate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    private var binding: FragmentDictionaryBinding by autoCleared()

    private val viewModel: DictionaryViewModel by viewModels {
        val repo = (activity?.application as KanjiApplication).lexemeRepository
        viewModelFactory {
            DictionaryViewModel(
                GetLexemesUseCase(repo),
                SearchLexemesUseCase(repo),
                FilterLexemesUseCase(repo),
                SearchInFilteredLexemesUseCase(repo),
                DeleteLexemesFromSelectionUseCase(repo)
            )
        }
    }

    private var lexemeAdapter = LexemeAdapter().apply {
        itemClickedCallback = { lexeme ->
            if (isSelectionActive)
                tracker?.select(lexeme.id)
            else {
                //TODO Display details fragment
                Toast.makeText(requireContext(), "Details fragment should open", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private lateinit var tracker: SelectionTracker<Long>

    private var isSelectionActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDictionaryBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        setupTracker()
        setupObservers()

        restoreInstanceState(savedInstanceState)

        handleBackPressed()
    }

    private fun setupRecyclerView() = binding.lexemeRecyclerView.apply {
        adapter = lexemeAdapter
        itemAnimator = null
        edgeEffectFactory = StretchEdgeEffectFactory()
    }

    private fun setupListeners() = with(binding) {
        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val range = appBarLayout.totalScrollRange
            val displayRatio = (range + verticalOffset).toFloat() / range

            // Ranges from 0 to 255 as the displayRatio goes from 0 to 0.5 | 0 above 0.5
            collapsedTitle.alpha = 1 - (displayRatio * 2)

            // Ranges from 1 to 0 as the displayRatio goes from 1 to 0.5 | 0 below 0.5
            expandedTitleLayout.alpha = 1 - ((1 - displayRatio) * 2)
        }

        dictionaryToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        filterLexemes.setOnClickListener {
            FilterLexemesDialogFragment
                .newInstance(viewModel.getFilter().toLongArray())
                .setSuccessCallback { filter ->
                    viewModel.updateFilter(filter)
                }
                .show(parentFragmentManager, FILTER_LEXEMES_DIALOG_TAG)
        }

        sortLexemes.setOnClickListener {
            val (sortField, sortOrder) = viewModel.getSortingState()
            SortLexemesDialogFragment
                .newInstance(sortField, sortOrder)
                .setSuccessCallback { field, order ->
                    viewModel.updateSorting(field, order)
                }
                .show(parentFragmentManager, SORT_LEXEMES_DIALOG_TAG)
        }

        addLexeme.setOnClickListener {
            val action = DictionaryFragmentDirections.actionDictionaryFragmentToAddLexemeFragment()
            safeNavigate(action)
        }

        searchView.setOnQueryTextListener (object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.updateQuery(newText.lowercase())
                return false
            }
        })

        selectAllCheckbox.setOnClickListener {
            if (!selectAllCheckbox.isChecked) clearSelection()
            else {
                val items = viewModel.lexemes.value!!.map { it.id }
                tracker.setItemsSelected(items, true)
            }
        }

        deleteButton.setOnClickListener {
            val selection = tracker.selection
            ConfirmDeletionDialogFragment.newInstance(selection.size())
                .setSuccessCallback { viewModel.deleteLexemesFromSelection(selection.toList()) }
                .show(parentFragmentManager, DELETE_DIALOG_TAG)
        }
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "lexeme_selection", binding.lexemeRecyclerView,
            ItemKeyProvider(binding.lexemeRecyclerView), LexemeDetailsLookup(binding.lexemeRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val selectionSize = tracker.selection.size()
                    viewModel.onSelectionChanged(selectionSize)
                }
            })

        lexemeAdapter.tracker = tracker
    }

    private fun setupObservers() {
        viewModel.lexemes.observe(viewLifecycleOwner) { lexemes ->
            binding.translationCount.text = resources.getQuantityString(
                R.plurals.translation_count,
                lexemes.size,
                lexemes.size
            )

            val filteringAvailable = lexemes.isNotEmpty() || viewModel.isFilteringOngoing()

            binding.filterLexemes.isVisible = filteringAvailable
            binding.sortLexemes.isVisible = filteringAvailable
            binding.searchView.isVisible = filteringAvailable

            binding.lexemeRecyclerView.layoutManager?.run {
                val state = onSaveInstanceState()
                lexemeAdapter.submitList(lexemes) {
                    onRestoreInstanceState(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lexemeValidationEvents.collectLatest { event ->
                when (event) {
                    is Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Success -> clearSelection(true)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectionSize.collect { selectionSize ->
                    if (!viewModel.isSelectionMode.value) return@collect
                    val title =
                        if (selectionSize == 0) resources.getString(R.string.dictionary_title_empty_selection)
                        else resources.getString(R.string.dictionary_title_selection, selectionSize)
                    binding.expandedTitle.text = title
                    binding.collapsedTitle.text = title
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSelectionMode.collect { isSelectionMode ->
                    if (isSelectionMode == isSelectionActive) return@collect
                    isSelectionActive = isSelectionMode

                    binding.run {
                        if (!isSelectionMode) {
                            expandedTitle.text = resources.getString(R.string.dictionary_title_default)
                            collapsedTitle.text = resources.getString(R.string.dictionary_title_default)
                        }

                        dictionaryToolbar.navigationIcon =
                            if (isSelectionMode) null
                            else ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)

                        translationCount.isVisible = !isSelectionMode
                        selectAllLayout.isVisible = isSelectionMode
                        addLexeme.isVisible = !isSelectionMode
                        sortLexemes.isVisible = !isSelectionMode
                        filterLexemes.isVisible = !isSelectionMode
                        deleteLayout.isVisible = isSelectionMode

                        lexemeAdapter.isSelectionMode = isSelectionMode
                        val layoutManager = lexemeRecyclerView.layoutManager as LinearLayoutManager
                        val first = layoutManager.findFirstVisibleItemPosition()
                        val last = layoutManager.findLastVisibleItemPosition()

                        for (index in first..last) {
                            val view = layoutManager.findViewByPosition(index)
                            view?.findViewById<CheckBox>(R.id.selection_checkbox)?.isVisible = isSelectionMode
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allSelected.collect { allSelected ->
                    binding.selectAllCheckbox.isChecked = allSelected
                }
            }
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.run {
            tracker.onRestoreInstanceState(savedInstanceState)

            val dialog = parentFragmentManager.findFragmentByTag(DELETE_DIALOG_TAG) as? ConfirmDeletionDialogFragment
            dialog?.setSuccessCallback { viewModel.deleteLexemesFromSelection(tracker.selection.toList()) }
        }
    }

    private fun clearSelection(disableSelectionMode: Boolean = false) {
        if (disableSelectionMode) viewModel.disableSelectionMode()
        tracker.clearSelection()
    }

    private fun handleBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isSelectionActive)
                        clearSelection(true)
                    else if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker.onSaveInstanceState(outState)
    }
}