package fr.steph.kanji.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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
import fr.steph.kanji.ui.dialog.ADD_LESSON_DIALOG_TAG
import fr.steph.kanji.ui.dialog.AddLessonDialogFragment
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

    private lateinit var dropdownAdapter: ArrayAdapter<String>

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
        dropdownAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayListOf(resources.getString(R.string.select_lesson), resources.getString(R.string.none))
        ) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                when (position) {
                    0 -> with(TextView(context)) {
                        height = 0
                        visibility = View.GONE
                        return this
                    }

                    count - 1 -> with(super.getDropDownView(position, null, parent) as TextView) {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.add_lexeme_half_margin)
                        return this
                    }

                    else -> return super.getDropDownView(position, null, parent)
                }
            }

            override fun isEnabled(position: Int): Boolean {
                return position != count - 1
            }
        }

        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.lessonSpinner.adapter = dropdownAdapter
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

        binding.lessonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.onEvent(AddLexemeFormEvent.LessonChanged(position - 1L))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewModel.allLessons.observe(viewLifecycleOwner) { allLessons ->
            val lessonNames = allLessons.map { lesson ->
                resources.getString(R.string.lesson_display, lesson.number, lesson.label)
            }

            dropdownAdapter.run {
                clear()
                add(resources.getString(R.string.select_lesson))
                add(resources.getString(R.string.none))
                addAll(lessonNames)
                add(resources.getString(R.string.add_lesson))
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