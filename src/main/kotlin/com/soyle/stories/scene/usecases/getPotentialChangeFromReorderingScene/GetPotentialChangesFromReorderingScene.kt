package com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene

import java.util.*

interface GetPotentialChangesFromReorderingScene {

    suspend operator fun invoke(sceneId: UUID, index: Int, output: OutputPort)

    interface OutputPort {
        fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene)
    }

}