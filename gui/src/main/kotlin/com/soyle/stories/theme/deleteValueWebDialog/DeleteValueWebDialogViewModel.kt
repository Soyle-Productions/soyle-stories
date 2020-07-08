package com.soyle.stories.theme.deleteValueWebDialog

data class DeleteValueWebDialogViewModel(
    val title: String,
    val message: String,
    val doNotShowLabel: String,
    val errorMessage: String?,
    val deleteButtonLabel: String,
    val cancelButtonLabel: String,
    val doDefaultAction: Boolean
)