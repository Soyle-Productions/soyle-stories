package com.soyle.stories.usecase.scene.delete

import com.soyle.stories.domain.scene.Scene

interface GetPotentialChangesFromDeletingScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receivePotentialChangesFromDeletingScene(response: PotentialChangesOfDeletingScene)
    }
}