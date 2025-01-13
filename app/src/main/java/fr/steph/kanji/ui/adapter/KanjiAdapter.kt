package fr.steph.kanji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.data.model.Kanji
import fr.steph.kanji.databinding.ItemKanjiBinding

class KanjiAdapter : ListAdapter<Kanji, KanjiAdapter.KanjiViewHolder>(KanjiDiffUtil()) {
    var itemClickedCallback: ((Kanji) -> Unit) = {}

    class KanjiViewHolder(private val binding: ItemKanjiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(kanji: Kanji) {
            binding.kanji = kanji
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): KanjiViewHolder {
                val binding = ItemKanjiBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return KanjiViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanjiViewHolder =
        KanjiViewHolder.from(parent)

    override fun onBindViewHolder(holder: KanjiViewHolder, position: Int) {
        val currentKanji = getItem(position)
        holder.apply {
            bind(currentKanji)
            itemView.setOnClickListener { itemClickedCallback(currentKanji) }
        }
    }

    class KanjiDiffUtil : DiffUtil.ItemCallback<Kanji>() {
        override fun areItemsTheSame(oldItem: Kanji, newItem: Kanji) =
            newItem.id == oldItem.id

        override fun areContentsTheSame(oldItem: Kanji, newItem: Kanji) =
            newItem == oldItem
    }
}