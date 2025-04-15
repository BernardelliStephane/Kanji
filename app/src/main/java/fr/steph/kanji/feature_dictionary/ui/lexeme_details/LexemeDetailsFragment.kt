package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import fr.steph.kanji.KanjiApplication
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.util.Resource
import fr.steph.kanji.core.ui.util.StrokeOrderDiagramHelper.createStrokeOrderDiagram
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.core.ui.util.viewModelFactory
import fr.steph.kanji.core.util.extension.log
import fr.steph.kanji.core.util.extension.showToast
import fr.steph.kanji.databinding.FragmentLexemeDetailsBinding
import fr.steph.kanji.feature_dictionary.domain.exception.MissingStrokeOrderDiagramException
import fr.steph.kanji.feature_dictionary.domain.use_case.GetKanjiInfoUseCase
import fr.steph.kanji.feature_dictionary.domain.use_case.GetStrokeFilenamesUseCase
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
        val filenames = viewModel.getCharactersStrokeFilenames(lexeme.characters)
        filenames.forEachIndexed { index, filename ->
            showStrokeOrderDiagrams(filename, lexeme.characters[index])
        }

        setupObservers()
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

    private fun showStrokeOrderDiagrams(filename: String?, character: Char) {
        val diagramSize = resources.displayMetrics.density.toInt() * 100
        val screenSize = resources.displayMetrics.widthPixels
        val remainingSpace = screenSize % diagramSize

        val params = binding.strokeOrderLayout.layoutParams as MarginLayoutParams
        params.marginStart = remainingSpace / 2

        try {
            if (filename == null) throw MissingStrokeOrderDiagramException()
            val diagram = createStrokeOrderDiagram(requireContext(), filename, diagramSize)
            displayStrokeOrderDiagram(diagram)
        }
        catch (e: Exception) {
            log(e.message.toString())
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
        binding.strokeOrderLayout.isVisible = true
    }

    private fun displayStrokeOrderDiagram(diagram: List<Bitmap>) {
        val flexboxLayout = FlexboxLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            flexWrap = FlexWrap.WRAP
        }

        val inflater = LayoutInflater.from(requireContext())

        for (bitmap in diagram) {
            val itemView = inflater.inflate(R.layout.item_stroke, flexboxLayout, false)
            val imageView = itemView.findViewById<ImageView>(R.id.stroke_image)
            imageView.setImageBitmap(bitmap)
            flexboxLayout.addView(itemView)
        }

        binding.strokeOrderLayout.addView(flexboxLayout)
        binding.strokeOrderLayout.isVisible = true
    }
}