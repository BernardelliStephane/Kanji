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
import fr.steph.kanji.core.data.model.jisho.Jisho
import fr.steph.kanji.core.data.model.jisho.JishoResponse
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.util.extension.getQuantityStringZero
import fr.steph.kanji.databinding.FragmentTranslationSelectionBinding
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.adapter.TranslationAdapter
import fr.steph.kanji.feature_dictionary.ui.add_lexeme.viewmodel.TranslationSelectionViewModel
import kotlinx.coroutines.launch

class TranslationSelectionFragment : Fragment(R.layout.fragment_translation_selection) {

    private var binding: FragmentTranslationSelectionBinding by autoCleared()

    private val viewModel: TranslationSelectionViewModel by viewModels()

    private val args: TranslationSelectionFragmentArgs by navArgs()

    private lateinit var adapter: TranslationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTranslationSelectionBinding.bind(view)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        val translations = Gson().fromJson<List<Jisho>>(
            args.translations,
            object : TypeToken<List<Jisho>>() {}.type
        )

        adapter = TranslationAdapter(translations) { translation ->
            viewModel.setSelectedTranslation(translation)
        }

        binding.translationRecyclerView.adapter = adapter

        binding.confirmButton.setOnClickListener {
            with(findNavController()) {
                previousBackStackEntry?.savedStateHandle?.set("selectedTranslation", viewModel.selectedTranslation.value)
                popBackStack()
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedTranslation.collect { translation ->
                    binding.confirmButton.isEnabled = translation != null
                }
            }
        }
    }
}