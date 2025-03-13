package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.databinding.FragmentAddLexemeBinding
import fr.steph.kanji.ui.adapter.SpinnerAdapter
import fr.steph.kanji.ui.dialog.ADD_LESSON_DIALOG_TAG
import fr.steph.kanji.ui.dialog.AddLessonDialogFragment
import fr.steph.kanji.ui.uistate.AddLexemeFormEvent
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddLexemeViewModel
import fr.steph.kanji.ui.viewmodel.ApiLexemeViewModel
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.viewmodel.LexemeViewModel.ValidationEvent.Success
import fr.steph.kanji.utils.extension.hideSpinnerDropDown
import fr.steph.kanji.utils.extension.setMaxVisibleItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS = 10

class AddLexemeFragment : Fragment(R.layout.fragment_add_lexeme) {

    private var binding: FragmentAddLexemeBinding by autoCleared()

    private val viewModel: AddLexemeViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLexemeViewModel(app.lessonRepository, app.lexemeRepository, app.apiRepository)
        }
    }

    private lateinit var dropdownAdapter: SpinnerAdapter

    private var addedLesson: Lesson? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddLexemeBinding.bind(view)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupUI() {
        dropdownAdapter = SpinnerAdapter(requireContext()) {
            binding.lessonSpinner.hideSpinnerDropDown()
            AddLessonDialogFragment()
                .setFailureCallback { binding.lessonSpinner.performClick() }
                .setSuccessCallback { addedLesson = it }
                .show(parentFragmentManager, ADD_LESSON_DIALOG_TAG)
        }

        binding.lessonSpinner.adapter = dropdownAdapter
    }

    private fun setupListeners() {
        binding.buttonCancel.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.buttonConfirm.setOnClickListener {
            viewModel.submitData { lexeme ->
                // TODO Display "Update translation" dialog
                Toast.makeText(requireContext(), resources.getString(R.string.duplicate_characters_error), Toast.LENGTH_SHORT).show()
            }
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

        binding.lessonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.onEvent(AddLexemeFormEvent.LessonChanged(id))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewModel.allLessons.observe(viewLifecycleOwner) { allLessons ->
            dropdownAdapter.updateLessons(allLessons)

            if (allLessons.size > SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS)
                binding.lessonSpinner.setMaxVisibleItems(layoutInflater, SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS)

            addedLesson?.let {
                val index = allLessons.indexOf(it)
                binding.lessonSpinner.setSelection(index + 2)
            }
        }

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
            viewModel.lexemeValidationEvents.collectLatest { event ->
                when (event) {
                    is Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Success -> Navigation.findNavController(requireView()).navigateUp()
                }
            }
        }
    }
}