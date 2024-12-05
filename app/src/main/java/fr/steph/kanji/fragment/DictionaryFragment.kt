package fr.steph.kanji.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    // Binding
    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDictionaryBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}