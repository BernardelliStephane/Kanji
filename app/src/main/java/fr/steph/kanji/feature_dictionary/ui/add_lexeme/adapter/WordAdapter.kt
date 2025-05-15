package fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.util.extension.kanaToRomaji
import fr.steph.kanji.databinding.ItemWordBinding

class WordAdapter(
    private var wordList: List<Word> = emptyList(),
    private val onWordSelected: (Word?) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    private var selectedWord: Word? = null
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = wordList[position]
        holder.bind(currentWord, position == selectedPosition)

        holder.itemView.setOnClickListener {
            selectWord(currentWord, position)
        }
    }

    override fun getItemCount() = wordList.size

    private fun selectWord(word: Word, position: Int) {
        val previousSelected = selectedPosition

        selectedPosition = if (word == selectedWord) null else position
        selectedWord = if (word == selectedWord) null else word

        onWordSelected(selectedWord)

        previousSelected?.let { notifyItemChanged(it) }
        selectedPosition?.let { notifyItemChanged(it) }
    }

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word, isSelected: Boolean) {
            val firstVariant = word.variants.firstOrNull()
            binding.writtenText.text = firstVariant?.written ?: "-"
            binding.pronouncedText.text = firstVariant?.pronounced ?: "-"
            binding.romajiText.text = firstVariant?.pronounced?.kanaToRomaji() ?: "-"
            binding.glossesText.text = word.meanings.flatMap { it.glosses }.joinToString()

            if (isSelected)
                binding.root.setCardBackgroundColor(Color.GRAY)
            else binding.root.setCardBackgroundColor(Color.WHITE)
        }
    }
}