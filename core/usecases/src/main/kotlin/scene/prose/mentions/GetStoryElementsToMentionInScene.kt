package com.soyle.stories.usecase.scene.prose.mentions

import com.soyle.stories.domain.scene.Scene

interface GetStoryElementsToMentionInScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receiveStoryElementsToMentionInScene(availability: AvailableStoryElementsToMentionInScene)
    }
}