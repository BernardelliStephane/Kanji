package fr.steph.kanji.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import fr.steph.kanji.R
import fr.steph.kanji.data.model.Lesson

const val NO_LESSON_ID = 0L
const val SELECT_LESSON_ID = -1L
const val ADD_LESSON_ID = -2L

class SpinnerAdapter(
    context: Context,
    private val onAddLessonClicked: (() -> Unit) = {}
): ArrayAdapter<Lesson> (context, android.R.layout.simple_spinner_item, mutableListOf()) {

    private val lessons = mutableListOf<Lesson>()

    init {
        resetList()
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    fun updateLessons(lessons: List<Lesson>) {
        resetList(lessons)
    }

    private fun resetList(lessons: List<Lesson> = emptyList()) {
        this.lessons.clear()
        this.lessons.add(Lesson(SELECT_LESSON_ID, context.getString(R.string.select_lesson)))
        this.lessons.add(Lesson(NO_LESSON_ID, context.getString(R.string.none)))
        this.lessons.addAll(lessons)
        this.lessons.add(Lesson(ADD_LESSON_ID, context.getString(R.string.add_lesson)))
        notifyDataSetChanged()
    }

    override fun getCount(): Int = lessons.size

    override fun getItem(position: Int): Lesson = lessons[position]

    override fun getItemId(position: Int): Long = lessons[position].number

    override fun isEnabled(position: Int): Boolean = getItemId(position) != ADD_LESSON_ID

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        createView(position, convertView, parent, android.R.layout.simple_spinner_item)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val dropdownLayout = android.R.layout.simple_spinner_dropdown_item

        return when (getItemId(position)) {
            SELECT_LESSON_ID -> TextView(context).apply {
                height = 0
                isVisible = false
            }

            ADD_LESSON_ID -> createView(position, null, parent, dropdownLayout).apply {
                val text = findViewById<TextView>(android.R.id.text1)
                text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                text.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.add_lexeme_half_margin)
                setOnClickListener { onAddLessonClicked() }
            }

            else -> createView(position, null, parent, dropdownLayout)
        }
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutRes, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        val lesson = lessons[position]

        textView.text = when(lesson.number) {
           SELECT_LESSON_ID, NO_LESSON_ID, ADD_LESSON_ID -> lesson.label
            else -> context.getString(R.string.lesson_display, lesson.number, lesson.label)
        }

        return view
    }
}