package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.scene.Scene
import java.util.*

interface GetPotentialChangesFromReorderingScene {

    suspend operator fun invoke(sceneId: Scene.Id, index: Int, output: OutputPort): Throwable?

    fun interface OutputPort {
        suspend fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene)
    }

}