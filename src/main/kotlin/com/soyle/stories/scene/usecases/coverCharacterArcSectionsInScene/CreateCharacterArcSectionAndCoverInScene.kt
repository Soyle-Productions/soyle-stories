package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

interface CreateCharacterArcSectionAndCoverInScene {

    suspend fun listAvailableCharacterArcSectionTypesForCharacterArc()
    suspend operator fun invoke()

}