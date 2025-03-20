package fr.steph.kanji.feature_dictionary.ui.add_lexeme.uistate

data class AddLessonFormState(
    var number: String = "",
    var numberErrorRes: Int? = null,
    var label: String = "",
    var labelErrorRes: Int? = null,
    var isSubmitting: Boolean = false,
)