package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentHomeBinding
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.utils.extension.safeNavigate

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initializeViews()
    }

    private fun initializeViews() {
        binding.apply {
            buttonDictionary.view.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDictionaryFragment()
                safeNavigate(action)
            }
        }
    }
}