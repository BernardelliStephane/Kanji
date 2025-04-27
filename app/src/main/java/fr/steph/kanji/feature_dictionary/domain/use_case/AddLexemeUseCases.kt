package fr.steph.kanji.feature_dictionary.domain.use_case

data class AddLexemeUseCases(
    val getKanjiInfo: GetKanjiInfoUseCase,
    val getCompoundInfo: GetCompoundInfoUseCase,
    val upsertLexeme: UpsertLexemeUseCase,
    val getLexemeByCharacters: GetLexemeByCharactersUseCase,
    val getLessons: GetLessonsUseCase,
    val insertLesson: InsertLessonUseCase
)