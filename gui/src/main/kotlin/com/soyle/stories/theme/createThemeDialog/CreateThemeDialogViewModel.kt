package com.soyle.stories.theme.createThemeDialog

data class CreateThemeDialogViewModel(
    val title: String,
    val nameFieldLabel: String,
    val errorMessage: String?,
    val created: Boolean
)