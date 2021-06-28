package com.soyle.stories.project.dialogs

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory

data class ActiveDialogsViewModel(
    val confirmDeleteCharacter: ActiveDialogViewModel<RemoveCharacterFromStory.ConfirmationRequest> = ActiveDialogViewModel(null)
)

class ActiveDialogViewModel<DialogData : Any>(val dialogData: DialogData?) {
    val isOpen: Boolean
        get() = dialogData != null
    fun closed(): ActiveDialogViewModel<DialogData> = ActiveDialogViewModel(null)
}