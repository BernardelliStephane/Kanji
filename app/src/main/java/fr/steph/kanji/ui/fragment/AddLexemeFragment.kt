package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.databinding.FragmentAddLexemeBinding
import fr.steph.kanji.ui.uistate.AddLexemeFormEvent
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddLexemeViewModel
import fr.steph.kanji.ui.viewmodel.ApiLexemeViewModel
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddLexemeFragment : Fragment(R.layout.fragment_add_lexeme) {

    private var binding: FragmentAddLexemeBinding by autoCleared()

    private val viewModel: AddLexemeViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLexemeViewModel(app.lessonRepository, app.lexemeRepository, app.apiRepository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddLexemeBinding.bind(view)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.buttonCancel.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.charactersInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.CharactersChanged(it.toString()))
        }

        binding.romajiInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.RomajiChanged(it.toString()))
        }

        binding.meaningInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.MeaningChanged(it.toString()))
        }
    }

    private fun setupObservers() {
        viewModel.lastKanjiFetch.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is ApiLexemeViewModel.Resource.Success ->
                    viewModel.onEvent(AddLexemeFormEvent.KanjiFetched(resource.data))

                is ApiLexemeViewModel.Resource.Error ->
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()

                else -> {}
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                when (event) {
                    is Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Success -> Navigation.findNavController(requireView()).navigateUp()
                }
            }
        }
    }
}