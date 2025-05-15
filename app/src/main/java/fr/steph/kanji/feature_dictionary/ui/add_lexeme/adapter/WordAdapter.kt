package fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.util.extension.kanaToRomaji
import fr.steph.kanji.databinding.ItemWordBinding

class WordAdapter(
    private var wordList: List<Word> = emptyList(),
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    var selectedWord: Word? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = wordList[position]
        holder.bind(currentWord)
    }

    override fun getItemCount() = wordList.size

    @SuppressLint("NotifyDataSetChanged")
    private fun selectWord(word: Word) {
        selectedWord =
            if (word == selectedWord) null
            else word
        notifyDataSetChanged()
    }

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            val firstVariant = word.variants.firstOrNull()
            binding.writtenText.text = firstVariant?.written ?: "-"
            binding.pronouncedText.text = firstVariant?.pronounced ?: "-"
            binding.romajiText.text = firstVariant?.pronounced?.kanaToRomaji() ?: "-"
            binding.glossesText.text = word.meanings.flatMap { it.glosses }.joinToString()

            if (word == selectedWord)
                binding.root.setCardBackgroundColor(Color.GRAY)
            else binding.root.setCardBackgroundColor(Color.WHITE)

            binding.root.setOnClickListener {
                selectWord(word)
            }
        }
    }
}