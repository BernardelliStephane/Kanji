package fr.steph.kanji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.data.model.Lesson
import fr.steph.kanji.databinding.ItemLessonBinding

class LessonAdapter(private val lessonList: List<Lesson>) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {
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

    class LessonViewHolder(private val binding: ItemLessonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson, isSelected: Boolean) {
            binding.lessonLabel.text = lesson.label
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