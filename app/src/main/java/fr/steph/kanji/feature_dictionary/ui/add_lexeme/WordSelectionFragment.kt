package fr.steph.kanji.feature_dictionary.ui.add_lexeme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.databinding.FragmentWordSelectionBinding
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter.WordAdapter

class WordSelectionFragment : Fragment(R.layout.fragment_word_selection) {

    private var binding: FragmentWordSelectionBinding by autoCleared()

    private lateinit var adapter: WordAdapter

    private val args: WordSelectionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWordSelectionBinding.bind(view)

        val words: List<Word> = Gson().fromJson(args.words, object : TypeToken<List<Word>>() {}.type)

        adapter = WordAdapter(words)

        binding.wordRecyclerView.adapter = adapter

        binding.confirmButton.setOnClickListener {
            if (adapter.selectedWord == null)
                return@setOnClickListener

            with(findNavController()) {
                previousBackStackEntry?.savedStateHandle?.set("selectedWord", adapter.selectedWord)
                popBackStack()
            }
        }
    }
}