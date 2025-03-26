package fr.steph.kanji.feature_dictionary.ui.dictionary

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.ui.util.ItemKeyProvider
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.StretchEdgeEffectFactory
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.DELETE_DIALOG_TAG
import fr.steph.kanji.core.util.FILTER_LEXEMES_DIALOG_TAG
import fr.steph.kanji.core.util.SORT_LEXEMES_DIALOG_TAG
import fr.steph.kanji.core.util.extension.getQuantityStringZero
import fr.steph.kanji.core.util.extension.navigateUp
import fr.steph.kanji.core.util.extension.safeNavigate
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.util.LexemeDetailsLookup
import fr.steph.kanji.feature_dictionary.ui.dictionary.adapter.LexemeAdapter
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.ConfirmDeletionDialogFragment
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.FilterLexemesDialogFragment
import fr.steph.kanji.feature_dictionary.ui.dictionary.dialog.SortLexemesDialogFragment
import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.DictionaryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    private var binding: FragmentDictionaryBinding by autoCleared()

    private val viewModel: DictionaryViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            DictionaryViewModel(app.dictionaryUseCases)
        }
    }

    private lateinit var lexemeAdapter: LexemeAdapter

    private lateinit var tracker: SelectionTracker<Long>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDictionaryBinding.bind(view)

        setupUI()
        setupTracker()
        setupListeners()
        setupObservers()

        restoreInstanceState(savedInstanceState)

        handleBackPressed()
    }

    private fun setupUI() {
        lexemeAdapter = LexemeAdapter().apply {
            itemClickedCallback = { lexeme ->
                if (viewModel.isSelectionActive())
                    tracker?.select(lexeme.id)
                else {
                    val action = DictionaryFragmentDirections.actionDictionaryFragmentToLexemeDetailsFragment(lexeme)
                    safeNavigate(action)
                }
            }
        }
        binding.lexemeRecyclerView.apply {
            adapter = lexemeAdapter
            itemAnimator = null
            edgeEffectFactory = StretchEdgeEffectFactory()
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
                    if (!viewModel.isSelectionActive()) return

                    binding.selectAllCheckbox.isChecked = selectionSize == lexemeAdapter.itemCount

                    val title = getQuantityStringZero(
                        resId = R.plurals.dictionary_title_selection,
                        zeroResId = R.string.dictionary_title_selection_empty,
                        quantity = selectionSize
                    )
                    binding.expandedTitle.text = title
                    binding.collapsedTitle.text = title
                }
            })

        lexemeAdapter.tracker = tracker
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
            navigateUp()
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
            if (!selectAllCheckbox.isChecked)
                return@setOnClickListener clearSelection()

            val items = lexemeAdapter.currentList.map { it.id }
            tracker.setItemsSelected(items, true)
        }

        deleteButton.setOnClickListener {
            val selection = tracker.selection
            ConfirmDeletionDialogFragment.newInstance(selection.size())
                .setSuccessCallback { viewModel.deleteLexemesFromSelection(selection.toList()) }
                .show(parentFragmentManager, DELETE_DIALOG_TAG)
        }
    }

    private fun setupObservers() {
        viewModel.lexemes.observe(viewLifecycleOwner) { lexemes ->
            val filteringAvailable = lexemes.isNotEmpty() || viewModel.isFilteringOngoing()

            with(binding) {
                translationCount.text = resources.getQuantityString(
                    R.plurals.translation_count,
                    lexemes.size,
                    lexemes.size
                )

                filterLexemes.isVisible = filteringAvailable
                sortLexemes.isVisible = filteringAvailable
                searchView.isVisible = filteringAvailable

                lexemeRecyclerView.layoutManager?.run {
                    val state = onSaveInstanceState()
                    lexemeAdapter.submitList(lexemes) {
                        onRestoreInstanceState(state)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSelectionMode.collect { isSelectionMode ->
                    applySelectionMode(isSelectionMode)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                when (event) {
                    is Resource.Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Resource.Success -> clearSelection(true)
                }
            }
        }
    }

    private fun applySelectionMode(isSelectionMode: Boolean) = with(binding) {
        if (!isSelectionMode) {
            val title = resources.getString(R.string.dictionary_title_default)
            expandedTitle.text = title
            collapsedTitle.text = title
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

    private fun clearSelection(disableSelectionMode: Boolean = false) {
        if (disableSelectionMode) viewModel.disableSelectionMode()
        tracker.clearSelection()
    }

    private fun handleBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isSelectionActive())
                        clearSelection(true)
                    else if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.run {
            tracker.onRestoreInstanceState(savedInstanceState)

            val dialog = parentFragmentManager.findFragmentByTag(DELETE_DIALOG_TAG) as? ConfirmDeletionDialogFragment
            dialog?.setSuccessCallback { viewModel.deleteLexemesFromSelection(tracker.selection.toList()) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker.onSaveInstanceState(outState)
    }
}