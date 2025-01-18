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
import fr.steph.kanji.databinding.FragmentAddLexemeBinding
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddLexemeViewModel
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddLexemeFragment : Fragment(R.layout.fragment_add_lexeme) {

    private var _binding: FragmentAddLexemeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddLexemeViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLexemeViewModel(app.repository, app.apiRepository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddLexemeBinding.bind(view)

        binding.viewModel = viewModel

        binding.buttonCancel.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }


        initObservers(view)
    }

    private fun initObservers(view: View) {
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