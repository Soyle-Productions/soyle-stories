package com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene

import java.util.*

interface GetAvailableCharacterArcSectionTypesForCharacterArc {

    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc)
    }
}