package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.Scene
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene

class SceneEditorController(
    private val sceneId: Scene.Id,
    private val getStoryElementsToMentionController: GetStoryElementsToMentionController,
    private val includeCharacterInSceneController: IncludeCharacterInSceneController,
    private val linkLocationToSceneController: LinkLocationToSceneController
) : SceneEditorViewListener {
    override fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput) {
        getStoryElementsToMentionController.getElementsForScene(sceneId, query, object : GetStoryElementsToMentionInScene.OutputPort {
            override suspend fun receiveStoryElementsToMentionInScene(response: GetStoryElementsToMentionInScene.ResponseModel) {
                output.invoke(response)
            }
        })
    }

    override fun useProseMentionInScene(mention: ProseMention<*>) {
        when (val id = mention.entityId.id) {
            is Character.Id -> includeCharacterInSceneController.includeCharacterInScene(sceneId.uuid.toString(), id.uuid.toString())
            is Location.Id -> linkLocationToSceneController.linkLocationToScene(sceneId.uuid.toString(), id.uuid.toString())
        }
    }
}