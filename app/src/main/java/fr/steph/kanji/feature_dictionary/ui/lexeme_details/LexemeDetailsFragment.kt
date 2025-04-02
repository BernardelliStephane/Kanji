package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.data.model.ApiKanji
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.StrokeOrderDiagramHelper.NoPathFoundException
import fr.steph.kanji.core.ui.util.StrokeOrderDiagramHelper.createStrokeOrderDiagram
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.extension.showToast
import fr.steph.kanji.core.util.extension.toKanjiVGFileNameFormat
import fr.steph.kanji.databinding.FragmentLexemeDetailsBinding
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class LexemeDetailsFragment : Fragment(R.layout.fragment_lexeme_details) {
    private var binding: FragmentLexemeDetailsBinding by autoCleared()
    private val args: LexemeDetailsFragmentArgs by navArgs()

    private val viewModel: LexemeDetailsViewModel by viewModels {
        val app = (activity?.application as KanjiApplication)
        viewModelFactory {
            LexemeDetailsViewModel(GetKanjiInfoUseCase(app.apiRepository))
        }
    }

    private lateinit var lexeme: Lexeme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLexemeDetailsBinding.bind(view)

        lexeme = args.lexeme

        viewModel.fetchKanji(lexeme.characters)

        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apiResponse.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        val kanji = response.data!!
                        showStrokeOrderDiagrams(kanji)
                    }

                    is Resource.Failure -> showToast(response.failureMessage)
                }
            }
        }
    }

    private fun showStrokeOrderDiagrams(kanji: ApiKanji) {
        val filename = kanji.unicode.toKanjiVGFileNameFormat()
        val width = binding.strokeOrder.width
        val height = binding.strokeOrder.height

        val diagrams: List<Bitmap>
        val diagramSize = Resources.getSystem().displayMetrics.density.toInt() * 100

        try {
            diagrams = createStrokeOrderDiagram(requireContext(), filename, diagramSize)
        }
        catch (e: Exception) {
            binding.strokeOrder.isVisible = false
            val message = when (e) {
                is IOException, is NoPathFoundException -> "No stroke order info for this kanji"
                else -> "An error occurred when displaying the stroke order diagrams"
            }
            return showToast(message)
        }

        binding.strokeOrder.setImageBitmap(diagrams[0])

        var currentStep = 0
        binding.strokeOrder.setOnClickListener {
            currentStep = (currentStep + 1) % diagrams.size
            binding.strokeOrder.setImageBitmap(diagrams[currentStep])
        }
    }
}