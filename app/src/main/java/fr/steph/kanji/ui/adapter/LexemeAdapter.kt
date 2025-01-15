package fr.steph.kanji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.databinding.ItemLexemeBinding

class LexemeAdapter : ListAdapter<Lexeme, LexemeAdapter.LexemeViewHolder>(LexemeDiffUtil()) {
    var itemClickedCallback: ((Lexeme) -> Unit) = {}

    class LexemeViewHolder(private val binding: ItemLexemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lexeme: Lexeme) {
            binding.lexeme = lexeme
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): LexemeViewHolder {
                val binding = ItemLexemeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return LexemeViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LexemeViewHolder =
        LexemeViewHolder.from(parent)

    override fun onBindViewHolder(holder: LexemeViewHolder, position: Int) {
        val currentLexeme = getItem(position)
        holder.apply {
            bind(currentLexeme)
            itemView.setOnClickListener { itemClickedCallback(currentLexeme) }
        }
    }

    class LexemeDiffUtil : DiffUtil.ItemCallback<Lexeme>() {
        override fun areItemsTheSame(oldItem: Lexeme, newItem: Lexeme) =
            newItem.id == oldItem.id

        override fun areContentsTheSame(oldItem: Lexeme, newItem: Lexeme) =
            newItem == oldItem
    }
}