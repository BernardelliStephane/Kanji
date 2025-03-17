package fr.steph.kanji.ui.feature_dictionary.add_lexeme.uistate

data class AddLessonFormState(
    var number: String = "",
    var numberErrorRes: Int? = null,
    var label: String = "",
    var labelErrorRes: Int? = null,
    var isSubmitting: Boolean = false,
)