package com.soyle.stories.usecase.character.listAllCharacterArcs

import java.util.*

/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:56 AM
 */
interface ListAllCharacterArcs {

    suspend operator fun invoke(projectId: UUID, outputPort: OutputPort)

    interface OutputPort {
        suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter)
    }

}