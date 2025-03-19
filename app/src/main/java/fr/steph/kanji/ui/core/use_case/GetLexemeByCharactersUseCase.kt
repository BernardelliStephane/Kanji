package fr.steph.kanji.ui.core.use_case

import fr.steph.kanji.data.repository.LexemeRepository

class GetLexemeByCharactersUseCase(private val repository: LexemeRepository) {

    suspend operator fun invoke(characters: String) =
        repository.getLexemeByCharacters(characters)
}