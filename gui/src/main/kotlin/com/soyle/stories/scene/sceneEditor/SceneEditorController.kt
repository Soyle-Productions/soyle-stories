package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Scene
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

class SceneEditorController(
    private val sceneId: Scene.Id,
    private val getStoryElementsToMentionController: GetStoryElementsToMentionController
) : SceneEditorViewListener {
    override fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput) {
        getStoryElementsToMentionController.getElementsForScene(sceneId, query, object : GetStoryElementsToMentionInScene.OutputPort {
            override suspend fun receiveStoryElementsToMentionInScene(response: GetStoryElementsToMentionInScene.ResponseModel) {
                output.invoke(response)
            }
        })
    }
}