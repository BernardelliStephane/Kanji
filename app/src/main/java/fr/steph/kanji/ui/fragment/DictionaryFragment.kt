package fr.steph.kanji.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.ui.adapter.KanjiAdapter
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.DictionaryViewModel
import fr.steph.kanji.utils.extension.safeNavigate

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DictionaryViewModel by viewModels {
        viewModelFactory {
            DictionaryViewModel((activity?.application as KanjiApplication).repository)
        }
    }

    private lateinit var kanjiAdapter: KanjiAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)

        initViews(view)
        initObservers()
    }

    private fun initViews(view: View) {
        kanjiAdapter = KanjiAdapter().apply {
            itemClickedCallback = { kanji ->
                //TODO Display details fragments
                Toast.makeText(requireContext(), "Details fragment should open", Toast.LENGTH_SHORT).show()
            }
        }

        binding.run {
            recyclerView.apply {
                adapter = kanjiAdapter
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

            addKanji.setOnClickListener {
                val action = DictionaryFragmentDirections.actionDictionaryFragmentToAddKanjiFragment()
                safeNavigate(action)
            }
        }
    }

    private fun initObservers() {
        viewModel.kanjis.observe(viewLifecycleOwner) { kanjis ->
            binding.kanjiCount.text = getString(R.string.kanji_count_text, kanjis.size)
            kanjiAdapter.submitList(kanjis)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}