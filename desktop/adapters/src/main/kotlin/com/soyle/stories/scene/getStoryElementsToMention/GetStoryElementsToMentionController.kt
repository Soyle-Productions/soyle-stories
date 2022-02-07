package com.soyle.stories.scene.getStoryElementsToMention

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.prose.mentions.GetStoryElementsToMentionInScene

interface GetStoryElementsToMentionController {

    fun getElementsForScene(sceneId: Scene.Id, output: GetStoryElementsToMentionInScene.OutputPort)

}