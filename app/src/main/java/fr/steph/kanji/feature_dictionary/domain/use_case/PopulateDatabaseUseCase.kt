package fr.steph.kanji.feature_dictionary.domain.use_case

import fr.steph.kanji.R
import fr.steph.kanji.core.data.repository.LessonRepository
import fr.steph.kanji.core.data.repository.LexemeRepository
import fr.steph.kanji.core.domain.model.DatabaseModel
import fr.steph.kanji.core.ui.util.DatabaseResource
import fr.steph.kanji.core.ui.util.Resource

class PopulateDatabaseUseCase(
    private val lessonRepository: LessonRepository,
    private val lexemeRepository: LexemeRepository
) {
    suspend operator fun invoke(database: DatabaseModel): DatabaseResource {
        lessonRepository.insertAll(database.lessons)
        lexemeRepository.insertAll(database.lexemes)

        val lessonResult = lessonRepository.insertAll(database.lessons)
        val lexemeResult = lexemeRepository.insertAll(database.lexemes)

        if(lessonResult.size != database.lessons.size || lexemeResult.size != database.lexemes.size)
            return Resource.Failure(R.string.dictionary_import_failure)

        return Resource.Success()
    }
}