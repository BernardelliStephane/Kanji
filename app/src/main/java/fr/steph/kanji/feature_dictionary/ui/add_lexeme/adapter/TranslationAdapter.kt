package fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.jisho.Jisho
import fr.steph.kanji.core.util.extension.kanaToRomaji
import fr.steph.kanji.databinding.ItemTranslationBinding

class TranslationAdapter(
    private var translationList: List<Jisho> = emptyList(),
    private val onTranslationSelected: (Jisho?) -> Unit
) : RecyclerView.Adapter<TranslationAdapter.TranslationViewHolder>() {

    private var selectedTranslation: Jisho? = null
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslationViewHolder {
        val binding = ItemTranslationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TranslationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TranslationViewHolder, position: Int) {
        val currentTranslation = translationList[position]
        holder.bind(currentTranslation, position == selectedPosition)

        holder.itemView.setOnClickListener {
            selectTranslation(currentTranslation, position)
        }
    }

    override fun getItemCount() = translationList.size

    private fun selectTranslation(translation: Jisho, position: Int) {
        val previousSelected = selectedPosition

        selectedPosition = if (translation == selectedTranslation) null else position
        selectedTranslation = if (translation == selectedTranslation) null else translation

        onTranslationSelected(selectedTranslation)

        previousSelected?.let { notifyItemChanged(it) }
        selectedPosition?.let { notifyItemChanged(it) }
    }

    inner class TranslationViewHolder(private val binding: ItemTranslationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(translation: Jisho, isSelected: Boolean) {
            binding.writingText.text = translation.writings.map { it.word }.distinct().joinToString()
            binding.pronouncedText.text = translation.writings.map { it.reading }.distinct().joinToString()
            binding.romajiText.text = translation.writings.map { it.reading.kanaToRomaji() }.distinct().joinToString()
            binding.meaningText.text = translation.senses.flatMap { it.meaning }.distinct().joinToString()

//            binding.writingText.text = word.variants.distinct().joinToString { it.written }
//            binding.pronouncedText.text = word.variants.distinct().joinToString { it.pronounced }
//            binding.romajiText.text = word.variants.distinct().joinToString { it.pronounced.kanaToRomaji() }
//            binding.meaningText.text = word.meanings.flatMap { it.glosses }.distinct().joinToString()

            if (isSelected)
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.grey))
            else
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
        }
    }
}