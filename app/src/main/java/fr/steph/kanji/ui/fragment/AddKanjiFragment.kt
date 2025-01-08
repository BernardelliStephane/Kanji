package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentAddKanjiBinding
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddKanjiViewModel

class AddKanjiFragment : Fragment(R.layout.fragment_add_kanji) {

    private var _binding: FragmentAddKanjiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddKanjiViewModel by viewModels {
        viewModelFactory {
            AddKanjiViewModel((activity?.application as KanjiApplication).repository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddKanjiBinding.bind(view)

        binding.viewModel = viewModel

        binding.buttonCancel.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}