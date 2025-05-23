package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.model.LexemeWithLesson
import fr.steph.kanji.core.util.extension.isCompound
import io.reactivex.rxjava3.core.Maybe

class GetLexemeByCharactersUseCase(private val repository: LexemeRepository) {

    operator fun invoke(characters: String, meaning: String): Maybe<LexemeWithLesson> {
        if (characters.isCompound())
            return repository.getLexemeByCharactersAndMeaning(characters, meaning)

        return repository.getLexemeByCharacters(characters)
    }
}