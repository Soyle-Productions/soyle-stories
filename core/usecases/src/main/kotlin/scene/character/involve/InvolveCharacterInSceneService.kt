package com.soyle.stories.usecase.scene.character.involve

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.character.list.PreviousMotivations
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.coroutineScope
import java.util.logging.Logger

class InvolveCharacterInSceneService(
    private val storyEvents: StoryEventRepository,
    private val scenes: SceneRepository
) : InvolveCharacterInScene {

    override suspend fun invoke(event: CharacterInvolvedInStoryEvent, output: InvolveCharacterInScene.OutputPort) {
        val storyEvent = storyEvents.getStoryEventById(event.storyEventId) ?: return
        val scene = storyEvent.sceneId?.let { scenes.getSceneById(it) } ?: return
        val source = CharacterInSceneSourceItem(storyEvent.id, storyEvent.name.value)
        val includedCharacter = scene.includedCharacters[event.characterId]
        if (includedCharacter != null) {
            output.sourceAddedToCharacterInScene(
                SourceAddedToCharacterInScene(
                    scene.id,
                    event.characterId,
                    source
                )
            )
            return
        }
        coroutineScope {
            output.characterInvolvedInScene(
                CharacterInvolvedInScene(
                    Project.Id().also { Logger.getGlobal().warning("NO TEST WRITTEN") },
                    scene.id,
                    event.characterId,
                    event.characterName,
                    PreviousMotivations(scene, scenes, this)
                        .getLastSetMotivation(event.characterId),
                    source
                )
            )
        }
    }

}