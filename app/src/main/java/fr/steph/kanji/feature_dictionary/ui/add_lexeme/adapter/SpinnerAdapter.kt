package fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.core.util.DEFAULT_LESSON_ID
import fr.steph.kanji.core.util.LESSON_NONE_ID

const val ADD_LESSON_ID = -3L

/**
 * Custom [ArrayAdapter] for displaying a list of [Lesson] items in a spinner.
 *
 * This adapter includes special entries for selecting no lesson, adding a new lesson,
 * and a default selection prompt.
 *
 * @param context The context used to inflate layouts and access resources.
 * @param onAddLessonClicked A callback invoked when the "Add Lesson" option is selected.
 */
class SpinnerAdapter(
    context: Context,
    private val onAddLessonClicked: (() -> Unit) = {}
): ArrayAdapter<Lesson> (context, android.R.layout.simple_spinner_item, mutableListOf()) {

    private val lessons = mutableListOf<Lesson>()

    init {
        resetList()
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    fun updateLessons(newLessons: List<Lesson>) {
        resetList(newLessons)
    }

    private fun resetList(newLessons: List<Lesson> = emptyList()) {
        lessons.clear()
        lessons.add(Lesson(DEFAULT_LESSON_ID, context.getString(R.string.select_lesson)))
        lessons.addAll(newLessons)
        lessons.add(Lesson(ADD_LESSON_ID, context.getString(R.string.add_lesson)))
        notifyDataSetChanged()
    }

    override fun getCount(): Int = lessons.size

    override fun getItem(position: Int): Lesson = lessons[position]

    override fun getItemId(position: Int): Long = lessons[position].number

    override fun getPosition(lesson: Lesson?): Int =
        lessons.indexOf(lesson).takeIf { it != -1 } ?: 0

    override fun isEnabled(position: Int): Boolean = getItemId(position) != ADD_LESSON_ID

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        createView(position, convertView, parent, android.R.layout.simple_spinner_item)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val dropdownLayout = android.R.layout.simple_spinner_dropdown_item

        return when (getItemId(position)) {
            DEFAULT_LESSON_ID -> TextView(context).apply {
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
           DEFAULT_LESSON_ID, LESSON_NONE_ID, ADD_LESSON_ID -> lesson.label
            else -> context.getString(R.string.lesson_display, lesson.number, lesson.label)
        }

        return view
    }
}