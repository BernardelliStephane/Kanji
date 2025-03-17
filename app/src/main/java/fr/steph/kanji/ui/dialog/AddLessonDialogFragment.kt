package fr.steph.kanji.ui.dialog

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
import fr.steph.kanji.domain.model.Lesson
import fr.steph.kanji.ui.uistate.AddLessonFormEvent
import fr.steph.kanji.ui.utils.autoCleared
import fr.steph.kanji.ui.utils.viewModelFactory
import fr.steph.kanji.ui.viewmodel.AddLessonViewModel
import fr.steph.kanji.ui.viewmodel.LessonViewModel
import fr.steph.kanji.ui.viewmodel.LessonViewModel.ValidationEvent.Failure
import fr.steph.kanji.ui.viewmodel.LessonViewModel.ValidationEvent.Success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import fr.steph.kanji.databinding.DialogAddLessonBinding as DialogAddLessonBinding1

const val ADD_LESSON_DIALOG_TAG = "add_lesson_dialog"

class AddLessonDialogFragment : DialogFragment(R.layout.dialog_add_lesson) {
    private var binding: DialogAddLessonBinding1 by autoCleared()

    private val viewModel: AddLessonViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            AddLessonViewModel(app.lessonRepository)
        }
    }

    private var failureCallback: (() -> Unit?)? = null
    private var successCallback: ((Lesson) -> Unit?)? = null

    private var validationState: LessonViewModel.ValidationEvent? = null

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
            viewModel.onEvent(AddLessonFormEvent.NumberChanged(it.toString()))
        }

        binding.lessonLabelInput.doAfterTextChanged {
            viewModel.onEvent(AddLessonFormEvent.LabelChanged(it.toString()))
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
            viewModel.lessonValidationEvents.collectLatest { event ->
                validationState = event

                when (event) {
                    is Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Success -> {
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
        if (validationState !is Success)
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