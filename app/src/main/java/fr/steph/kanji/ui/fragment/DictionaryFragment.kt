package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)

        initViews(view)
        initObservers()
    }

    private fun initViews(view: View) {
        binding.run {
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val range = appBarLayout.totalScrollRange
                val displayRatio = (range + verticalOffset).toFloat() / range
                expandedTitle.alpha =
                    if (displayRatio < 0.5) 0F
                    else 1 - ((1 - displayRatio) * 2)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}