package fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lesson
import fr.steph.kanji.databinding.ItemLessonBinding

class LessonAdapter : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {
    private var lessonList: List<Lesson> = emptyList()
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder =
        LessonViewHolder.from(parent)

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val currentLesson = lessonList[position]

        tracker?.let { tracker ->
            holder.itemView.setOnClickListener { tracker.select(currentLesson.number) }
            holder.bind(currentLesson, tracker.isSelected(getItemId(position)))
        }
    }

    override fun getItemCount() = lessonList.size

    override fun getItemId(position: Int) = lessonList[position].number

    fun updateLessons(newLessons: List<Lesson>) {
        lessonList = newLessons
        notifyItemRangeInserted(0, newLessons.size)
    }

    class LessonViewHolder(private val binding: ItemLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson, isSelected: Boolean) {
            binding.lessonLabel.text = itemView.context.getString(R.string.lesson_display, lesson.number, lesson.label)
            binding.selectionCheckbox.isChecked = isSelected
        }

        fun getLessonDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }

        companion object {
            fun from(parent: ViewGroup): LessonViewHolder {
                val binding = ItemLessonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return LessonViewHolder(binding)
            }
        }
    }
}