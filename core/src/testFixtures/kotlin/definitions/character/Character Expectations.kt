package com.soyle.stories.core.definitions.character

import com.soyle.stories.core.framework.`Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Character Expectations`(
    private val characterRepository: CharacterRepository,
    private val storyEventRepository: StoryEventRepository,

    private val `when`: `Character Steps`.When,

    private val sceneSteps: `Scene Steps`.Given
) : `Character Steps`.Given {
    override fun `a character`(named: String): `Character Steps`.Given.CreationExpectations = object :
        `Character Steps`.Given.CreationExpectations {
        override fun `has been created in the`(project: Project.Id): Character.Id {
            val id = runBlocking { characterRepository.listCharactersInProject(project) }
                .find { it.displayName.value == named }
                ?.id
            return id ?: `when`.`a character`(named).`is created in the`(project)
        }
    }

    override fun the(characterId: Character.Id): `Character Steps`.Given.StateExpectations = object :
        `Character Steps`.Given.StateExpectations {

        override fun `has been removed from the`(project: Project.Id) {
            val character = runBlocking { characterRepository.getCharacterById(characterId) }

            `when`.the(characterId).`is removed from the`(project)
        }

        override fun `has been involved in the`(storyEventId: StoryEvent.Id) {
            val storyEvent = runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }
            if (storyEvent.involvedCharacters.containsEntityWithId(characterId)) return
            `when`.the(characterId).`is involved in the`(storyEventId)
        }

        override fun `in the`(scene: Scene.Id): `Scene Character Steps`.Given.CharacterInSceneExpectations {
            return sceneSteps.characterInScene(scene, characterId)
        }

        override fun `has been removed from the`(storyEvent: StoryEvent.Id) {
            runBlocking { characterRepository.getCharacterById(characterId) } ?: return
            `when`.the(characterId).`is no longer involved in the`(storyEvent)
        }

        override fun `has been included in the`(scene: Scene.Id) {
            return sceneSteps.characterIncludedInScene(scene, characterId)
        }

        override fun `has been removed from the`(scene: Scene.Id) {
            return sceneSteps.characterRemovedFromScene(scene, characterId)
        }
    }
}