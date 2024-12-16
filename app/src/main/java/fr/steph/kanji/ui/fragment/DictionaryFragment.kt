package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding
import fr.steph.kanji.utils.extension.safeNavigate

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    // Binding
    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        binding.run {
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val range = appBarLayout.totalScrollRange
                val displayRatio = (range + verticalOffset).toFloat() / range
                expandedTitle.alpha =
                    if(displayRatio < 0.5) 0F
                    else 1 - ((1 - displayRatio) * 2)
            }

            addKanji.setOnClickListener {
                val action = DictionaryFragmentDirections.actionDictionaryFragmentToAddKanjiFragment()
                safeNavigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}