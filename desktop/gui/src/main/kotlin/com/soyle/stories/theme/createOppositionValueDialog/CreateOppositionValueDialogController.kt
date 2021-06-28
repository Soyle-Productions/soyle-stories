package com.soyle.stories.theme.createOppositionValueDialog

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController

class CreateOppositionValueDialogController(
    private val presenter: CreateOppositionValueDialogPresenter,
    private val addOppositionToValueWebController: AddOppositionToValueWebController
) : CreateOppositionValueDialogViewListener {

    override fun getValidState() {
        presenter.presentDialog()
    }

    override fun createOppositionValue(valueWebId: String, name: NonBlankString, linkedCharacterId: String) {
        addOppositionToValueWebController.addOppositionWithCharacter(valueWebId, name, linkedCharacterId)
    }

}