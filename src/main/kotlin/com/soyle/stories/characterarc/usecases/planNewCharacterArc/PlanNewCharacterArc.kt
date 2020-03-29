package com.soyle.stories.characterarc.usecases.planNewCharacterArc

import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 3:39 PM
 */
interface PlanNewCharacterArc {
    suspend operator fun invoke(
        characterId: UUID,
        name: String,
        outputPort: OutputPort
    )

    interface OutputPort {
        fun receivePlanNewCharacterArcFailure(failure: Exception)
        fun receivePlanNewCharacterArcResponse(response: com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem)
    }
}