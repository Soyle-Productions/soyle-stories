package com.soyle.stories.scene.usecases.getStoryElementsToMention

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Scene

interface GetStoryElementsToMentionInScene {

    suspend operator fun invoke(sceneId: Scene.Id, query: NonBlankString, output: OutputPort)

    class ResponseModel(matchingStoryElements: List<MatchingStoryElement>) : List<MatchingStoryElement> by matchingStoryElements
    data class MatchingStoryElement(val entityId: EntityId<*>, val name: String)

    interface OutputPort {
        suspend fun receiveStoryElementsToMentionInScene(response: ResponseModel)
    }
}