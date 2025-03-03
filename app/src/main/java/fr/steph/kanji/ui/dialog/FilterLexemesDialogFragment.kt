package fr.steph.kanji.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.databinding.DialogFilterLexemesBinding
import fr.steph.kanji.ui.adapter.LessonAdapter
import fr.steph.kanji.ui.utils.StretchEdgeEffectFactory
import fr.steph.kanji.ui.utils.autoCleared

const val FILTER_LEXEMES_DIALOG_TAG = "filter_lexemes_dialog"

class FilterLexemesDialogFragment : DialogFragment(R.layout.dialog_filter_lexemes) {
    private var binding: DialogFilterLexemesBinding by autoCleared()
    private var confirmCallback: ((ArrayList<Int>) -> Unit?)? = null
    private lateinit var lessons: ArrayList<Int>

    private val items = List(100) { Lesson(it + 1L, "Label ${it + 1}") }
    private var lessonAdapter = LessonAdapter(items)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogFilterLexemesBinding.bind(view)

        lessons = requireArguments().getIntegerArrayList(ARG_INITIAL_SELECTION)!!

        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            decorView.background.alpha = 0
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.lessonRecyclerView.apply {
            adapter = lessonAdapter
            edgeEffectFactory = StretchEdgeEffectFactory()
        }
    }

    private fun setupListeners() = with(binding) {
        dialogCancelButton.setOnClickListener { dismiss() }
        dialogDoneButton.setOnClickListener {
            confirmCallback?.invoke(lessons)
            dismiss()
        }
    }

    fun setConfirmCallback(callback: (ArrayList<Int>) -> Unit): FilterLexemesDialogFragment {
        confirmCallback = callback
        return this
    }

    companion object {
        private const val ARG_INITIAL_SELECTION = "initial_lessons"

        fun newInstance(lessons: ArrayList<Int>): FilterLexemesDialogFragment {
            return FilterLexemesDialogFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(ARG_INITIAL_SELECTION, lessons)
                }
            }
        }
    }
}