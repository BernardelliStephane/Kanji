package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentAddKanjiBinding
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddKanjiViewModel
import fr.steph.kanji.ui.viewmodel.AddKanjiViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.viewmodel.AddKanjiViewModel.ValidationEvent.Success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        initializeObservers(view)
    }

    private fun initializeObservers(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                when (event) {
                    is Failure -> {
                        Snackbar.make(view, event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    }
                    is Success -> {
                        Navigation.findNavController(view).navigateUp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}