/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 5:03 PM
 */
package com.soyle.stories.characterarc.planCharacterArcDialog

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import java.util.*

class PlanCharacterArcDialogController(
    private val planNewCharacterArc: PlanNewCharacterArc,
    private val planNewCharacterArcOutputPort: PlanNewCharacterArc.OutputPort
) : PlanCharacterArcDialogViewListener {
    override suspend fun planCharacterArc(characterId: String, name: String) {
        planNewCharacterArc.invoke(UUID.fromString(characterId), name, planNewCharacterArcOutputPort)

    }
}