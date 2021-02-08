package com.soyle.stories.characterarc.planCharacterArcDialog

import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController

class PlanCharacterArcDialogController(
    private val planNewCharacterArcController: PlanNewCharacterArcController
) : PlanCharacterArcDialogViewListener {

    override fun planCharacterArc(characterId: String, name: String) {
        planNewCharacterArcController.planCharacterArc(characterId, name) {}
    }

}