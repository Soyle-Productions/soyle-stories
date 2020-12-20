package com.soyle.stories.scene.getStoryElementsToMention

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

interface GetStoryElementsToMentionController {

    fun getElementsForScene(sceneId: Scene.Id, query: NonBlankString, output: GetStoryElementsToMentionInScene.OutputPort)

}