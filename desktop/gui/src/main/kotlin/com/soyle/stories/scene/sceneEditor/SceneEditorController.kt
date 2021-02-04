package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.*
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.prose.proseEditor.OnLoadMentionReplacementsOutput
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionController
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse

class SceneEditorController(
    private val sceneId: Scene.Id,
    private val getStoryElementsToMentionController: GetStoryElementsToMentionController,
    private val includeCharacterInSceneController: IncludeCharacterInSceneController,
    private val linkLocationToSceneController: LinkLocationToSceneController,
    private val listOptionsToReplaceMentionController: ListOptionsToReplaceMentionController
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

    override fun loadMentionReplacements(entityId: MentionedEntityId<*>, output: OnLoadMentionReplacementsOutput) {
        listOptionsToReplaceMentionController.listOptionsToReplaceMention(sceneId, entityId, object : ListOptionsToReplaceMentionInSceneProse.OutputPort {
            override suspend fun receiveOptionsToReplaceMention(response: ListOptionsToReplaceMentionInSceneProse.ResponseModel<*>) {
                output.loadedReplacements(response.options)
            }
        })
    }
}