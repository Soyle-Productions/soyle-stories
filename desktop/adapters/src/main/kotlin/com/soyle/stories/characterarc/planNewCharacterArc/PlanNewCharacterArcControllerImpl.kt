package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.usecase.character.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class PlanNewCharacterArcControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val planNewCharacterArc: PlanNewCharacterArc,
    private val planNewCharacterArcOutputPort: PlanNewCharacterArc.OutputPort
) : PlanNewCharacterArcController {

    override fun planCharacterArc(characterId: String, name: String, onError: (Throwable) -> Unit) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            //try {
                planNewCharacterArc.invoke(preparedCharacterId, name, planNewCharacterArcOutputPort)
            //} catch (n: Nothing) {}
        }

    }
}