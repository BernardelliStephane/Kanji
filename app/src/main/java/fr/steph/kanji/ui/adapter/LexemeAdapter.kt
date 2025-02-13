package fr.steph.kanji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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
    var isSelectionMode = false

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LexemeViewHolder =
        LexemeViewHolder.from(parent)

    override fun onBindViewHolder(holder: LexemeViewHolder, position: Int) {
        val currentLexeme = getItem(position)
        holder.itemView.setOnClickListener { itemClickedCallback(currentLexeme) }

        tracker?.let {
            holder.bind(currentLexeme, isSelectionMode, it.isSelected(getItemId(position)))
        }
    }

    override fun onViewAttachedToWindow(holder: LexemeViewHolder) {
        val isSelected = tracker?.isSelected(holder.itemId)
        if(isSelected != null)
            holder.setSelection(isSelectionMode, isSelected)
    }

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    class LexemeViewHolder(private val binding: ItemLexemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lexeme: Lexeme, isSelectionMode: Boolean, isSelected: Boolean) {
            binding.lexemeCharacters.text = lexeme.characters
            binding.lexemeMeaning.text = lexeme.meaning

            setSelection(isSelectionMode, isSelected)

            binding.executePendingBindings()
        }

        fun setSelection(isSelectionMode: Boolean, isSelected: Boolean) {
            binding.selectionCheckbox.isVisible = isSelectionMode

            binding.isActivated = isSelected
            binding.selectionCheckbox.isChecked = isSelected
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
