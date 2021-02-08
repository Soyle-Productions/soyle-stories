package com.soyle.stories.scene.getStoryElementsToMention

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.getStoryElementsToMention.GetStoryElementsToMentionInScene

class GetStoryElementsToMentionControllerImpl(
    private val getStoryElementsToMentionInScene: GetStoryElementsToMentionInScene,
    private val threadTransformer: ThreadTransformer
) : GetStoryElementsToMentionController {

    override fun getElementsForScene(
        sceneId: Scene.Id,
        query: NonBlankString,
        output: GetStoryElementsToMentionInScene.OutputPort
    ) {
        threadTransformer.async {
            getStoryElementsToMentionInScene.invoke(sceneId, query, output)
        }
    }

}