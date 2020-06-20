package com.soyle.stories.theme.createSymbolDialog

data class CreateSymbolDialogViewModel(
    val title: String,
    val nameFieldLabel: String,
    val errorMessage: String?,
    val created: Boolean
)