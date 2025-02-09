package fr.steph.kanji.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.ui.adapter.LexemeAdapter
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.DictionaryViewModel
import fr.steph.kanji.ui.utils.LexemeDetailsLookup
import fr.steph.kanji.ui.utils.LexemeKeyProvider
import fr.steph.kanji.utils.extension.safeNavigate

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DictionaryViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            DictionaryViewModel(app.repository)
        }
    }

    private lateinit var lexemeAdapter: LexemeAdapter

    private var tracker: SelectionTracker<Long>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)

        initViews(view)
        initObservers()

        tracker?.onRestoreInstanceState(savedInstanceState)

    }

    private fun initViews(view: View) {
        lexemeAdapter = LexemeAdapter().apply {
            itemClickedCallback = { lexeme ->
                //TODO Display details fragment
                Toast.makeText(requireContext(), "Details fragment should open", Toast.LENGTH_SHORT).show()
            }
        }

        binding.run {
            recyclerView.apply {
                adapter = lexemeAdapter
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }

            tracker = SelectionTracker.Builder(
                "lexeme_selection",
                recyclerView,
                LexemeKeyProvider(recyclerView),
                LexemeDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(SelectionPredicates.createSelectAnything()
            ).build()

            tracker?.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        val selectionSize = tracker?.selection!!.size()
                    }
                })

            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val range = appBarLayout.totalScrollRange
                val displayRatio = (range + verticalOffset).toFloat() / range

                // Ranges from 0 to 255 as the displayRatio goes from 0 to 0.5 | 0 above 0.5
                val opacity = if(displayRatio > 0.5) 0
                    else 255 - (((displayRatio * 255).toInt() * 2) - 255)

                val color = Color.argb(opacity, 0, 0, 0)
                val colorStateList = ColorStateList.valueOf(color)

                collapsingToolbarLayout.setExpandedTitleTextColor(colorStateList)
                collapsingToolbarLayout.setCollapsedTitleTextColor(color)

                // Ranges from 1 to 0 as the displayRatio goes from 1 to 0.5 | 0 below 0.5
                expandedTitle.alpha = 1 - ((1 - displayRatio) * 2)
            }

            dictionaryToolbar.setNavigationOnClickListener {
                Navigation.findNavController(view).navigateUp()
            }

            addLexeme.setOnClickListener {
                val action = DictionaryFragmentDirections.actionDictionaryFragmentToAddLexemeFragment()
                safeNavigate(action)
            }
        }

        lexemeAdapter.tracker = tracker
    }

    private fun initObservers() {
        viewModel.lexemes.observe(viewLifecycleOwner) { lexemes ->
            binding.translationCount.text = resources.getQuantityString(R.plurals.translation_count_text, lexemes.size, lexemes.size)
            lexemeAdapter.submitList(lexemes)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}