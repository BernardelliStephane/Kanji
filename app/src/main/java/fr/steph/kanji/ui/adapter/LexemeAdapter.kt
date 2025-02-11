package fr.steph.kanji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.data.model.Lexeme
import fr.steph.kanji.databinding.ItemLexemeBinding

class LexemeAdapter : ListAdapter<Lexeme, LexemeAdapter.LexemeViewHolder>(LexemeDiffUtil()) {
    var itemClickedCallback: ((Lexeme) -> Unit) = {}
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LexemeViewHolder =
        LexemeViewHolder.from(parent)

    override fun onBindViewHolder(holder: LexemeViewHolder, position: Int) {
        val currentLexeme = getItem(position)
        val isSelected = tracker?.isSelected(currentLexeme.id.toLong()) ?: false
        holder.bind(currentLexeme, isSelected) {
            itemClickedCallback(currentLexeme)
        }
    }

    class LexemeViewHolder(private val binding: ItemLexemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lexeme: Lexeme, isActivated: Boolean = false, itemClickedCallback: () -> Unit) {
            binding.isActivated = isActivated
            binding.run {
                root.setOnClickListener { itemClickedCallback() }
                lexemeCharacters.text = lexeme.characters
                lexemeMeaning.text = lexeme.meaning
                executePendingBindings()
            }
        }

        fun getLexemeDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long = itemId
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

    class LexemeDiffUtil : DiffUtil.ItemCallback<Lexeme>() {
        override fun areItemsTheSame(oldItem: Lexeme, newItem: Lexeme) =
            newItem.id == oldItem.id

        override fun areContentsTheSame(oldItem: Lexeme, newItem: Lexeme) =
            newItem == oldItem
    }
}
