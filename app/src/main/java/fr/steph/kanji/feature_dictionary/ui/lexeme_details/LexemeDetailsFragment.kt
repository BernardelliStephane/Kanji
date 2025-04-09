package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
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
        filenames.forEach { showStrokeOrderDiagrams(it) }

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

    private fun showStrokeOrderDiagrams(filename: String?) {
        val diagramSize = Resources.getSystem().displayMetrics.density.toInt() * 100

        try {
            if (filename == null) throw MissingStrokeOrderDiagramException()
            val diagram = createStrokeOrderDiagram(requireContext(), filename, diagramSize)
            displayStrokeOrderDiagram(diagram)
        }
        catch (e: Exception) {
            log(e.message.toString())
            val message = when (e) {
                is IOException, is MissingStrokeOrderDiagramException -> "No stroke order info for this kanji"
                else -> "An error occurred when displaying the stroke order diagrams"
            }
            return showToast(message)
        }
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