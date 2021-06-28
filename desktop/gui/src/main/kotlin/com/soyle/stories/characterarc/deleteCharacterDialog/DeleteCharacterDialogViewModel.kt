package com.soyle.stories.characterarc.deleteCharacterDialog

data class DeleteCharacterDialogViewModel(
    val title: String,
    val header: String,
    val message: String,
    val doDefaultAction: Boolean,
    val deleteButtonLabel: String,
    val cancelButtonLabel: String
)