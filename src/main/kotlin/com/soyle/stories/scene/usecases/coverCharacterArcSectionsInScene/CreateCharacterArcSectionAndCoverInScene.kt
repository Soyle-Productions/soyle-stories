package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

interface CreateCharacterArcSectionAndCoverInScene {

    suspend fun listCharacterArcSectionTypesForNewArc()
    suspend operator fun invoke()

}