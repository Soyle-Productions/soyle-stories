package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import java.util.*

class IncludedCharacterInSceneController(
    private val sceneId: String,
    private val storyEventId: String,
    private val characterId: String,
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterFromStoryEventController: RemoveCharacterFromStoryEventController,
    private val setMotivationForCharacterInSceneController: SetMotivationForCharacterInSceneController,
    private val coverArcSectionsInSceneController: CoverArcSectionsInSceneController,
    private val getAvailableCharacterArcsForCharacterInScene: GetAvailableCharacterArcsForCharacterInScene,
    private val openToolController: OpenToolController,
    private val presenter: IncludedCharacterInScenePresenter
) : IncludedCharacterInSceneViewListener {

    override fun removeCharacter() {
        removeCharacterFromStoryEventController.removeCharacter(storyEventId, characterId)
    }

    override fun setMotivation(motivation: String) {
        setMotivationForCharacterInSceneController.setMotivationForCharacter(sceneId, characterId, motivation)
    }

    override fun resetMotivation() {
        setMotivationForCharacterInSceneController.clearMotivationForCharacter(sceneId, characterId)
    }

    override fun openSceneDetails(sceneId: String) {
        openToolController.openSceneDetailsTool(sceneId)
    }

    override fun getAvailableCharacterArcSections() {
        val sceneId = UUID.fromString(sceneId)
        val characterId = UUID.fromString(characterId)
        threadTransformer.async {
            getAvailableCharacterArcsForCharacterInScene.invoke(
                sceneId,
                characterId,
                presenter
            )
        }
    }

    override fun coverCharacterArcSectionInScene(
        characterArcSectionIds: List<String>,
        sectionsToUnCover: List<String>
    ) {
        coverArcSectionsInSceneController.coverCharacterArcSectionInScene(
            sceneId, characterId, characterArcSectionIds, sectionsToUnCover
        )
    }

}