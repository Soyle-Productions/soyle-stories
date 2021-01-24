package com.soyle.stories.theme.deleteSymbolDialog

data class DeleteSymbolDialogViewModel(
    val title: String,
    val message: String,
    val doNotShowLabel: String,
    val errorMessage: String?,
    val deleteButtonLabel: String,
    val cancelButtonLabel: String,
    val doDefaultAction: Boolean
)