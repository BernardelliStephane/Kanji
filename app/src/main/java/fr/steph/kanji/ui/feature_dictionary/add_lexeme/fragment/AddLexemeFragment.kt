package fr.steph.kanji.ui.feature_dictionary.add_lexeme.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.domain.model.Lesson
import fr.steph.kanji.databinding.FragmentAddLexemeBinding
import fr.steph.kanji.databinding.StubAddLexemeBinding
import fr.steph.kanji.ui.core.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.adapter.SpinnerAdapter
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.dialog.ADD_LESSON_DIALOG_TAG
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.dialog.AddLessonDialogFragment
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.dialog.ConfirmLexemeUpdateDialogFragment
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.dialog.LEXEME_UPDATE_DIALOG_TAG
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.uistate.AddLexemeFormEvent
import fr.steph.kanji.ui.core.util.autoCleared
import fr.steph.kanji.ui.core.util.viewModelFactory
import fr.steph.kanji.ui.core.viewmodel.LexemeViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.core.viewmodel.LexemeViewModel.ValidationEvent.Success
import fr.steph.kanji.ui.feature_dictionary.add_lexeme.viewmodel.AddLexemeViewModel
import fr.steph.kanji.util.extension.hideSpinnerDropDown
import fr.steph.kanji.util.extension.setMaxVisibleItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS = 10

class AddLexemeFragment : Fragment(R.layout.fragment_add_lexeme) {

    private var binding: FragmentAddLexemeBinding by autoCleared()
    private var stubBinding: StubAddLexemeBinding by autoCleared()

    private val viewModel: AddLexemeViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLexemeViewModel(GetKanjiInfoUseCase(app.apiRepository), app.lessonRepository, app.lexemeRepository)
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
        binding.lessonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.onEvent(AddLexemeFormEvent.LessonChanged(id))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.charactersInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.CharactersChanged(it.toString()))
        }

        binding.searchKanji.setOnClickListener {
            viewModel.onEvent(AddLexemeFormEvent.Search)
        }

        binding.romajiInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.RomajiChanged(it.toString()))
        }

        binding.meaningInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeFormEvent.MeaningChanged(it.toString()))
        }

        binding.stubKanjiForm.setOnInflateListener { _, inflatedView ->
            stubBinding = DataBindingUtil.bind(inflatedView)!!
            stubBinding.viewModel = viewModel
            stubBinding.lifecycleOwner = viewLifecycleOwner
        }

        binding.buttonCancel.setOnClickListener {
            if (viewModel.uiState.value.isUpdating)
                return@setOnClickListener viewModel.resetUi()
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.buttonConfirm.setOnClickListener {
            viewModel.onEvent(AddLexemeFormEvent.Submit { duplicateLexeme ->
                binding.charactersInput.clearFocus()
                binding.romajiInput.clearFocus()
                binding.meaningInput.clearFocus()
                ConfirmLexemeUpdateDialogFragment()
                    .setSuccessCallback {
                        val lessonIndex = viewModel.updateUi(duplicateLexeme)
                        binding.lessonSpinner.setSelection(lessonIndex + 2)
                    }
                    .show(parentFragmentManager, LEXEME_UPDATE_DIALOG_TAG)
            })
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
                is AddLexemeViewModel.Resource.Success -> {
                    binding.stubKanjiForm.viewStub?.inflate()
                    viewModel.onEvent(AddLexemeFormEvent.KanjiFetched(resource.data))
                }

                is AddLexemeViewModel.Resource.Error ->
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()

                else -> {}
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lexemeValidationEvents.collectLatest { event ->
                when (event) {
                    is Failure -> {
                        Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                        viewModel.stopSubmission()
                    }
                    is Success -> Navigation.findNavController(requireView()).navigateUp()
                }
            }
        }
    }
}