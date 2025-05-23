package fr.steph.kanji.feature_dictionary.ui.add_lexeme.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.ui.util.LessonResource
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.extension.setupDialogWindow
import fr.steph.kanji.databinding.DialogAddLessonBinding
import fr.steph.kanji.feature_dictionary.domain.use_case.GetLessonNumbersUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.InsertLessonUseCase
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate.AddLessonEvent
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.AddLessonViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddLessonDialogFragment : DialogFragment(R.layout.dialog_add_lesson) {
    private var binding: DialogAddLessonBinding by autoCleared()

    private val viewModel: AddLessonViewModel by viewModels {
        val repo = (activity?.application as KanjiApplication).lessonRepository
        viewModelFactory {
            AddLessonViewModel(GetLessonNumbersUseCase(repo), InsertLessonUseCase(repo))
        }
    }

    private var failureCallback: (() -> Unit?)? = null
    private var successCallback: ((Lesson) -> Unit?)? = null

    private var validationState: LessonResource? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogAddLessonBinding.bind(view)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupDialogWindow(alignBottom = false)
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
            viewModel.onEvent(AddLessonEvent.Submit)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationEvents.collectLatest { event ->
                validationState = event

                when (event) {
                    is Resource.Failure -> Snackbar.make(requireView(), event.failureMessage, Snackbar.LENGTH_SHORT).show()
                    is Resource.Success -> {
                        successCallback?.invoke(event.data!!)
                        dismiss()
                    }
                }
            }
        }

        viewModel.lessonNumbers.observe(viewLifecycleOwner) { allLessonNumbers ->
            val nextLessonNumber =
                if (allLessonNumbers.isEmpty() || allLessonNumbers.last() < 0) "1"
                else allLessonNumbers.last().plus(1).toString()
            binding.lessonNumberInput.setText(nextLessonNumber)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (validationState !is Resource.Success)
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