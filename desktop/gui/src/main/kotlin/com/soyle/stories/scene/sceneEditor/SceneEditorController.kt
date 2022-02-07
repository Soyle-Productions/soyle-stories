package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.content.ProseContent
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.gui.View
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.prose.proseEditor.OnLoadMentionReplacementsOutput
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.sceneFrame.GetSceneFrameController
import com.soyle.stories.scene.sceneFrame.SetSceneFrameValueController
import com.soyle.stories.usecase.scene.prose.mentions.GetStoryElementsToMentionInScene

class SceneEditorController private constructor(
    private val sceneId: Scene.Id,
    private val getSceneFrameController: GetSceneFrameController,
    private val getStoryElementsToMentionController: GetStoryElementsToMentionController,
    private val linkLocationToSceneController: LinkLocationToSceneController,
    private val setSceneFrameValueController: SetSceneFrameValueController,
    private val presenter: SceneEditorPresenter
) : SceneEditorViewListener {

    interface Dependencies {
        val getSceneFrameController: GetSceneFrameController
        val getStoryElementsToMentionController: GetStoryElementsToMentionController
        val linkLocationToSceneController: LinkLocationToSceneController
        val setSceneFrameValueController: SetSceneFrameValueController
    }

    constructor(
        sceneId: Scene.Id,
        dependencies: Dependencies,
        view: View.Nullable<SceneEditorViewModel>
    ) : this(
        sceneId,
        dependencies.getSceneFrameController,
        dependencies.getStoryElementsToMentionController,
        dependencies.linkLocationToSceneController,
        dependencies.setSceneFrameValueController,
        SceneEditorPresenter(view)
    )

    override fun getValidState() {
        getSceneFrameController.getSceneFrame(sceneId, presenter)
    }

    override fun changeConflict(conflict: String) {
        setSceneFrameValueController.setSceneConflict(sceneId, conflict)
    }

    override fun changeResolution(resolution: String) {
        setSceneFrameValueController.setSceneResolution(sceneId, resolution)
    }

    override fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput) {
        getStoryElementsToMentionController.getElementsForScene(sceneId) {
            output.invoke(it)
        }
    }

    override fun useProseMentionInScene(mention: ProseContent.Mention<*>) {
        when (val id = mention.entityId.id) {
          //  is Character.Id -> includeCharacterInSceneController.includeCharacterInScene(sceneId.uuid.toString(), id.uuid.toString())
            is Location.Id -> linkLocationToSceneController.linkLocationToScene(sceneId, id)
        }
    }

    override fun loadMentionReplacements(entityId: MentionedEntityId<*>, output: OnLoadMentionReplacementsOutput) {
        getStoryElementsToMentionController.getElementsForScene(sceneId) {
            output.loadedReplacements(it)
        }
    }
}