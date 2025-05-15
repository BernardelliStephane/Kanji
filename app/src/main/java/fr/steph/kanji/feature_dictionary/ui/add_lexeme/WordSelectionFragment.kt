package fr.steph.kanji.feature_dictionary.ui.add_lexeme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.Word
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.databinding.FragmentWordSelectionBinding
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter.WordAdapter
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.WordSelectionViewModel
import kotlinx.coroutines.launch

class WordSelectionFragment : Fragment(R.layout.fragment_word_selection) {

    private var binding: FragmentWordSelectionBinding by autoCleared()

    private val viewModel: WordSelectionViewModel by viewModels()

    private val args: WordSelectionFragmentArgs by navArgs()

    private lateinit var adapter: WordAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWordSelectionBinding.bind(view)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        val words: List<Word> = Gson().fromJson(args.words, object : TypeToken<List<Word>>() {}.type)

        adapter = WordAdapter(words) { word ->
            viewModel.setSelectedWord(word)
        }

        binding.wordRecyclerView.adapter = adapter

        binding.confirmButton.setOnClickListener {
            with(findNavController()) {
                previousBackStackEntry?.savedStateHandle?.set("selectedWord", viewModel.selectedWord.value)
                popBackStack()
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedWord.collect { word ->
                    binding.confirmButton.isEnabled = word != null
                }
            }
        }
    }
}