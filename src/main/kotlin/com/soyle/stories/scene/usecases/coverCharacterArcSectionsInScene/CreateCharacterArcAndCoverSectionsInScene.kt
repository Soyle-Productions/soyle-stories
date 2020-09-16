package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

interface CreateCharacterArcAndCoverSectionsInScene {

    suspend fun listAvailableCharacterArcSectionTypesForCharacterArc(output: OutputPort)
    suspend operator fun invoke()

    interface OutputPort {

        suspend fun receiveRequiredCharacterArcSectionTypes(response: RequiredCharacterArcSectionTypes)

    }

}