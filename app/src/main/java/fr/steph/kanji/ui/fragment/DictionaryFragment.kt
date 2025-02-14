package fr.steph.kanji.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.ui.adapter.LexemeAdapter
import fr.steph.kanji.ui.utils.recyclerview_selection.LexemeDetailsLookup
import fr.steph.kanji.ui.utils.recyclerview_selection.LexemeKeyProvider
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.DictionaryViewModel
import fr.steph.kanji.utils.extension.safeNavigate
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DictionaryViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            DictionaryViewModel(app.repository)
        }
    }

    private var lexemeAdapter = LexemeAdapter().apply {
        itemClickedCallback = { lexeme ->
            //TODO Display details fragment
            Toast.makeText(requireContext(), "Details fragment should open", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private lateinit var tracker: SelectionTracker<Long>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)

        initViews(view)
        initTracker(savedInstanceState)
        initObservers()

        handleBackPressed()
    }

    private fun initViews(view: View) {
        binding.run {
            recyclerView.apply {
                adapter = lexemeAdapter
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }

            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val range = appBarLayout.totalScrollRange
                val displayRatio = (range + verticalOffset).toFloat() / range

                // Ranges from 0 to 255 as the displayRatio goes from 0 to 0.5 | 0 above 0.5
                val opacity = if (displayRatio > 0.5) 0
                else 255 - (((displayRatio * 255).toInt() * 2) - 255)

                val color = Color.argb(opacity, 0, 0, 0)
                val colorStateList = ColorStateList.valueOf(color)

                collapsingToolbarLayout.setExpandedTitleTextColor(colorStateList)
                collapsingToolbarLayout.setCollapsedTitleTextColor(color)

                // Ranges from 1 to 0 as the displayRatio goes from 1 to 0.5 | 0 below 0.5
                expandedTitleLayout.alpha = 1 - ((1 - displayRatio) * 2)
            }

            dictionaryToolbar.setNavigationOnClickListener {
                if (viewModel.selectionSize.value != 0)
                    tracker.clearSelection()
                else Navigation.findNavController(view).navigateUp()
            }

            addLexeme.setOnClickListener {
                val action =
                    DictionaryFragmentDirections.actionDictionaryFragmentToAddLexemeFragment()
                safeNavigate(action)
            }
        }
    }

    private fun initTracker(savedInstanceState: Bundle?) {
        tracker = SelectionTracker.Builder(
            "lexeme_selection", binding.recyclerView,
            LexemeKeyProvider(binding.recyclerView), LexemeDetailsLookup(binding.recyclerView),
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

        tracker.onRestoreInstanceState(savedInstanceState)
    }

    private fun initObservers() {
        viewModel.lexemes.observe(viewLifecycleOwner) { lexemes ->
            binding.translationCount.text = resources.getQuantityString(
                R.plurals.translation_count_text,
                lexemes.size,
                lexemes.size
            )
            lexemeAdapter.submitList(lexemes)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectionSize.collect { selectionSize ->
                    val isSelectionMode = selectionSize != 0
                    binding.run {
                        translationCount.isVisible = !isSelectionMode
                        selectAllLayout.isVisible = isSelectionMode
                        addLexeme.isVisible = !isSelectionMode
                        filterLexemes.isVisible = !isSelectionMode

                        deleteLayout.isVisible = isSelectionMode

                        val title = if (!isSelectionMode) "Dictionary" else "$selectionSize selected"

                        expandedTitle.text = title
                        collapsingToolbarLayout.title = title
                    }

                    if (isSelectionMode != lexemeAdapter.isSelectionMode) {
                        lexemeAdapter.isSelectionMode = isSelectionMode
                        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
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
    }

    private fun handleBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.selectionSize.value != 0) {
                        tracker.clearSelection()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}