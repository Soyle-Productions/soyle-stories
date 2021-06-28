package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.domain.character.Character
import com.soyle.stories.gui.View

class DeleteCharacterDialogController(
    view: View.Nullable<DeleteCharacterDialogViewModel>,
    private val removeCharacterFromStoryController: RemoveCharacterFromStoryController
) : DeleteCharacterDialogViewListener {

    private val presenter by lazy { DeleteCharacterDialogPresenter(view) }

    override fun getValidState(request: RemoveCharacterFromStory.ConfirmationRequest) {
        presenter.displayForCharacter(request.characterName)
    }

    override fun deleteCharacter(characterId: Character.Id, shouldShowAgain: Boolean) {
        removeCharacterFromStoryController.confirmRemoveCharacter(characterId)
    }

}