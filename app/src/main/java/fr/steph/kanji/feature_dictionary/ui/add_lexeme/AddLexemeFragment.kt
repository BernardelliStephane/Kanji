package fr.steph.kanji.feature_dictionary.ui.add_lexeme

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.LexemeWithLesson
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.ADD_LESSON_DIALOG_TAG
import fr.steph.kanji.core.util.LEXEME_UPDATE_DIALOG_TAG
import fr.steph.kanji.core.util.extension.hideSpinnerDropDown
import fr.steph.kanji.core.util.extension.navigateUp
import fr.steph.kanji.core.util.extension.setMaxVisibleItems
import fr.steph.kanji.databinding.FragmentAddLexemeBinding
import fr.steph.kanji.databinding.StubAddLexemeBinding
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter.SpinnerAdapter
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.dialog.AddLessonDialogFragment
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.dialog.ConfirmLexemeUpdateDialogFragment
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLexemeEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.AddLexemeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS = 10

class AddLexemeFragment : Fragment(R.layout.fragment_add_lexeme) {

    private var binding: FragmentAddLexemeBinding by autoCleared()
    private var stubBinding: StubAddLexemeBinding by autoCleared()

    private val viewModel: AddLexemeViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLexemeViewModel(app.addLexemeUseCases)
        }
    }

    private lateinit var dropdownAdapter: SpinnerAdapter

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
                .setFailureCallback {
                    binding.lessonSpinner.performClick()
                }
                .setSuccessCallback {
                    viewModel.onEvent(AddLexemeEvent.LessonAdded(it))
                }
                .show(parentFragmentManager, ADD_LESSON_DIALOG_TAG)
        }

        binding.lessonSpinner.adapter = dropdownAdapter
    }

    private fun setupListeners() {
        binding.lessonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.onEvent(AddLexemeEvent.LessonChanged(id))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.charactersInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeEvent.CharactersChanged(it.toString()))
        }

        binding.searchKanji.setOnClickListener {
            viewModel.onEvent(AddLexemeEvent.Fetch)
        }

        binding.romajiInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeEvent.RomajiChanged(it.toString()))
        }

        binding.meaningInput.doAfterTextChanged {
            viewModel.onEvent(AddLexemeEvent.MeaningChanged(it.toString()))
        }

        binding.stubKanjiForm.setOnInflateListener { _, inflatedView ->
            stubBinding = DataBindingUtil.bind(inflatedView)!!
            stubBinding.viewModel = viewModel
            stubBinding.lifecycleOwner = viewLifecycleOwner
        }

        binding.buttonCancel.setOnClickListener {
            if (!viewModel.uiState.value.isUpdating)
                return@setOnClickListener navigateUp()

            viewModel.resetUi()
            binding.lessonSpinner.setSelection(0)
        }

        binding.buttonConfirm.setOnClickListener {
            viewModel.onEvent(AddLexemeEvent.Submit {
                handleDuplicateCharacters(it)
            })
        }
    }

    private fun handleDuplicateCharacters(duplicateLexeme: LexemeWithLesson) {
        ConfirmLexemeUpdateDialogFragment()
            .setSuccessCallback {
                activity?.currentFocus?.clearFocus()

                val lesson = viewModel.updateUi(duplicateLexeme)
                val position = dropdownAdapter.getPosition(lesson)
                binding.lessonSpinner.setSelection(position)
            }
            .show(parentFragmentManager, LEXEME_UPDATE_DIALOG_TAG)
    }

    private fun setupObservers() {
        viewModel.allLessons.observe(viewLifecycleOwner) { allLessons ->
            dropdownAdapter.updateLessons(allLessons)

            if (allLessons.size > SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS)
                binding.lessonSpinner.setMaxVisibleItems(layoutInflater, SPINNER_DROPDOWN_MAX_VISIBLE_ITEMS)

            viewModel.getAddedLesson()?.let {
                val position = dropdownAdapter.getPosition(it)
                binding.lessonSpinner.setSelection(position)
                viewModel.resetAddedLesson()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apiResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.stubKanjiForm.viewStub?.inflate()
                        viewModel.onEvent(AddLexemeEvent.KanjiFetched(response.data!!))
                    }

                    is Resource.Failure ->
                        Toast.makeText(requireContext(), response.failureMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                when (event) {
                    is Resource.Failure ->
                        Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Resource.Success -> navigateUp()
                }
            }
        }
    }
}