package fr.steph.kanji.feature_dictionary.ui.lexeme_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import fr.steph.kanji.R
import fr.steph.kanji.core.domain.model.Lexeme
import fr.steph.kanji.core.ui.util.autoCleared
import fr.steph.kanji.databinding.FragmentLexemeDetailsBinding

class LexemeDetailsFragment : Fragment(R.layout.fragment_lexeme_details) {
    private var binding: FragmentLexemeDetailsBinding by autoCleared()

    private val args: LexemeDetailsFragmentArgs by navArgs()

    private lateinit var lexeme: Lexeme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLexemeDetailsBinding.bind(view)

        lexeme = args.lexeme

        binding.lexemeCharacters.text = lexeme.toString()
    }
}