package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.model.LexemeWithLesson
import io.reactivex.rxjava3.core.Maybe

class GetLexemeByCharactersUseCase(private val repository: LexemeRepository) {

    operator fun invoke(characters: String): Maybe<LexemeWithLesson> {
        return repository.getLexemeByCharacters(characters)
    }
}