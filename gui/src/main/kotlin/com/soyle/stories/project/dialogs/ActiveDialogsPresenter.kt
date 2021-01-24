package com.soyle.stories.project.dialogs

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterConfirmationReceiver
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.gui.View

class ActiveDialogsPresenter(
    private val view: View<ActiveDialogsViewModel>
) : RemoveCharacterConfirmationReceiver {

    override suspend fun receiveRemoveCharacterConfirmationRequest(request: RemoveCharacterFromStory.ConfirmationRequest) {
        view.updateOrInvalidated {
            copy(
                confirmDeleteCharacter = ActiveDialogViewModel(request)
            )
        }
    }

}