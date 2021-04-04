package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEventReceiver
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

class IncludeCharacterInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val includeCharacterInScene: IncludeCharacterInScene,
    private val includeCharacterInSceneOutputPort: IncludeCharacterInScene.OutputPort
) : IncludeCharacterInSceneController, IncludedCharacterInStoryEventReceiver {

    override fun includeCharacterInScene(sceneId: String, characterId: String) {
        val preparedSceneId = UUID.fromString(sceneId)
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            includeCharacterInScene.invoke(
                preparedSceneId, preparedCharacterId, includeCharacterInSceneOutputPort
            )
        }
    }

    override suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent) {
        includeCharacterInScene.invoke(
            AddCharacterToStoryEvent.ResponseModel(
                includedCharacterInStoryEvent.storyEventId,
                includedCharacterInStoryEvent.characterId
            ),
            includeCharacterInSceneOutputPort
        )
    }
}