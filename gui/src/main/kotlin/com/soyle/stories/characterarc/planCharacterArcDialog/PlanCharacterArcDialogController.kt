package com.soyle.stories.characterarc.planCharacterArcDialog

import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import java.util.*

class PlanCharacterArcDialogController(
    private val planNewCharacterArcController: PlanNewCharacterArcController
) : PlanCharacterArcDialogViewListener {

    override fun planCharacterArc(characterId: String, name: String) {
        planNewCharacterArcController.planCharacterArc(characterId, name) {}
    }

}