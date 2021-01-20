package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.entities.Character
import com.soyle.stories.gui.View

class DeleteCharacterDialogController(
    view: View.Nullable<DeleteCharacterDialogViewModel>,
    private val removeCharacterFromStoryController: RemoveCharacterFromStoryController
) : DeleteCharacterDialogViewListener {

    private val presenter by lazy { DeleteCharacterDialogPresenter(view) }

    override fun confirmCharacter(name: String) {
        presenter.displayForCharacter(name)
    }

    override fun deleteCharacter(characterId: Character.Id, shouldShowAgain: Boolean) {
        removeCharacterFromStoryController.removeCharacter(characterId.uuid.toString())
    }

}