package fr.steph.kanji.feature_dictionary.domain.use_case

data class DictionaryUseCases(
    val getLexemes: GetLexemesUseCase,
    val searchLexemes: SearchLexemesUseCase,
    val filterLexemes: FilterLexemesUseCase,
    val searchInFilteredLexemes: SearchInFilteredLexemesUseCase,
    val deleteLexemesFromSelection: DeleteLexemesFromSelectionUseCase,
    val getLessons: GetLessonsUseCase
)