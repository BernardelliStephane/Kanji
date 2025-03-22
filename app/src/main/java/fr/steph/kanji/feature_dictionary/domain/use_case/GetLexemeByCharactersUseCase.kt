package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository

class GetLexemeByCharactersUseCase(private val repository: LexemeRepository) {

    operator fun invoke(characters: String) =
        repository.getLexemeByCharacters(characters)
}