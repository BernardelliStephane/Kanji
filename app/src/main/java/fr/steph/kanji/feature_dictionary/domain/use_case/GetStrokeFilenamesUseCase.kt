package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.core.data.repository.StrokeOrderRepository

class GetStrokeFilenamesUseCase {

    operator fun invoke(characters: String): List<String?> {
        return characters.map(StrokeOrderRepository::getStrokeSvg)
    }
}