package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.KanjiApplication
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.StrokeOrderDiagramHelper.createStrokeOrderDiagram
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.extension.onLayoutReady
import fr.steph.kanji.core.util.extension.showToast
import fr.steph.kanji.databinding.FragmentLexemeDetailsBinding
import fr.steph.kanji.feature_dictionary.domain.exception.MissingStrokeOrderDiagramException
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetStrokeFilenamesUseCase
import fr.steph.kanji.feature_dictionary.ui.util.StrokeOrderDiagramConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class LexemeDetailsFragment : Fragment(R.layout.fragment_lexeme_details) {
    private var binding: FragmentLexemeDetailsBinding by autoCleared()
    private val args: LexemeDetailsFragmentArgs by navArgs()

    private val viewModel: LexemeDetailsViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            LexemeDetailsViewModel(GetKanjiInfoUseCase(app.apiRepository), GetStrokeFilenamesUseCase())
        }
    }

    private lateinit var lexeme: Lexeme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLexemeDetailsBinding.bind(view)

        lexeme = args.lexeme

        //viewModel.fetchKanji(lexeme.characters)

        setupStrokeOrderDiagrams()
        setupObservers()
    }

    private fun setupStrokeOrderDiagrams() {
        val filenames = viewModel.getCharactersStrokeFilenames(lexeme.characters)

        if (filenames.isNotEmpty()) {
            with(binding.strokeOrderLayout) {
                isVisible = true
                onLayoutReady {
                    val diagramConfig = StrokeOrderDiagramConfig.fromContext(requireContext(), this)

                    filenames.forEachIndexed { index, filename ->
                        displayStrokeOrderDiagram(filename, lexeme.characters[index], diagramConfig)
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apiResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        //val kanji = response.data!!
                        //showStrokeOrderDiagrams(kanji)
                    }

                    is Resource.Failure -> showToast(response.failureMessage)
                }
            }
        }
    }

    private fun displayStrokeOrderDiagram(filename: String?, character: Char, diagramConfig: StrokeOrderDiagramConfig) {
        try {
            val diagram = createStrokeOrderDiagram(requireContext(), filename, diagramConfig)
            binding.strokeOrderLayout.addView(diagram)
        }
        catch (e: Exception) {
            e.printStackTrace()
            displayErrorMessage(e, character)
        }
    }

    private fun displayErrorMessage(e: Exception, character: Char) {
        val message = when (e) {
            is IOException, is MissingStrokeOrderDiagramException -> "No stroke order info for $character"
            else -> "An error occurred when displaying the stroke order diagrams for $character"
        }
        val textView = TextView(requireContext()).apply {
            text = message
        }

        binding.strokeOrderLayout.addView(textView)
    }
}