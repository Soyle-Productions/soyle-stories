package com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene

import java.util.*

interface GetPotentialChangesFromReorderingScene {

    suspend operator fun invoke(sceneId: UUID, index: Int, output: OutputPort)

    interface OutputPort {
        fun receivePotentialChangesFromReorderingScene(response: PotentialChangesFromReorderingScene)
    }

}