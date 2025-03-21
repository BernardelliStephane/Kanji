package fr.steph.kanji.feature_dictionary.ui.add_lexeme.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonNumbersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLessonUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonEvent
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.AddLessonViewModel
import fr.steph.kanji.feature_dictionary.ui.dictionary.viewmodel.FilterLexemesViewModel.ValidationEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import fr.steph.kanji.databinding.DialogAddLessonBinding as DialogAddLessonBinding1

const val ADD_LESSON_DIALOG_TAG = "add_lesson_dialog"

class AddLessonDialogFragment : DialogFragment(R.layout.dialog_add_lesson) {
    private var binding: DialogAddLessonBinding1 by autoCleared()

    private val viewModel: AddLessonViewModel by viewModels {
        val repo = (activity?.application as KanjiApplication).lessonRepository
        viewModelFactory {
            AddLessonViewModel(GetLessonNumbersUseCase(repo), InsertLessonUseCase(repo))
        }
    }

    private var failureCallback: (() -> Unit?)? = null
    private var successCallback: ((Lesson) -> Unit?)? = null

    private var validationState: ValidationEvent? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogAddLessonBinding1.bind(view)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            decorView.background.alpha = 0
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.lessonNumberInput.doAfterTextChanged {
            viewModel.onEvent(AddLessonEvent.NumberChanged(it.toString()))
        }

        binding.lessonLabelInput.doAfterTextChanged {
            viewModel.onEvent(AddLessonEvent.LabelChanged(it.toString()))
        }

        binding.dialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.dialogConfirmButton.setOnClickListener {
            viewModel.submitData()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                validationState = event

                when (event) {
                    is ValidationEvent.Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is ValidationEvent.Success -> {
                        successCallback?.invoke(event.lesson)
                        dismiss()
                    }
                }
            }
        }

        viewModel.lessonNumbers.observe(viewLifecycleOwner) { allLessonNumbers ->
            val nextLessonNumber = if(allLessonNumbers.isEmpty()) "1"
                else allLessonNumbers.last().plus(1).toString()
            binding.lessonNumberInput.setText(nextLessonNumber)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (validationState !is ValidationEvent.Success)
            failureCallback?.invoke()
        super.onDismiss(dialog)
    }

    fun setFailureCallback(callback: () -> Unit): AddLessonDialogFragment {
        failureCallback = callback
        return this
    }

    fun setSuccessCallback(callback: (Lesson) -> Unit): AddLessonDialogFragment {
        successCallback = callback
        return this
    }
}