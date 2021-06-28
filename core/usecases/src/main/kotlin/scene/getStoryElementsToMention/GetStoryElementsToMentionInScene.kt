package com.soyle.stories.usecase.scene.getStoryElementsToMention

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString

interface GetStoryElementsToMentionInScene {

    suspend operator fun invoke(sceneId: Scene.Id, query: NonBlankString, output: OutputPort)

    class ResponseModel(matchingStoryElements: List<MatchingStoryElement>) : List<MatchingStoryElement> by matchingStoryElements
    data class MatchingStoryElement(val entityId: MentionedEntityId<*>, val name: String, val parentEntityName: String?)

    interface OutputPort {
        suspend fun receiveStoryElementsToMentionInScene(response: ResponseModel)
    }
}