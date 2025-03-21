package fr.steph.kanji

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import fr.steph.kanji.databinding.FragmentHomeBinding
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.util.extension.safeNavigate

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        buttonDictionary.view.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDictionaryFragment()
            safeNavigate(action)
        }
    }
}