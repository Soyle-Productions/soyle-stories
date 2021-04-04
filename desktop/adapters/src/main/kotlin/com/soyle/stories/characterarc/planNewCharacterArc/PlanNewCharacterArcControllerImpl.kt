package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.usecase.character.arc.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.common.ThreadTransformer
import kotlinx.coroutines.Job
import java.util.*

class PlanNewCharacterArcControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val planNewCharacterArc: PlanNewCharacterArc,
    private val planNewCharacterArcOutputPort: PlanNewCharacterArc.OutputPort
) : PlanNewCharacterArcController {

    override fun planCharacterArc(characterId: String, name: String): Job {
        val preparedCharacterId = UUID.fromString(characterId)
        return threadTransformer.async {
            //try {
                planNewCharacterArc.invoke(preparedCharacterId, name, planNewCharacterArcOutputPort)
            //} catch (n: Nothing) {}
        }

    }
}