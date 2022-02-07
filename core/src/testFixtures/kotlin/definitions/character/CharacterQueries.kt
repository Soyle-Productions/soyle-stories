package com.soyle.stories.core.definitions.character

import com.soyle.stories.core.framework.`Character Steps`
import com.soyle.stories.core.framework.storyevent.StoryEventCharacterSteps
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryUseCase
import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent
import kotlinx.coroutines.runBlocking

class CharacterQueries(
    private val storyEventPotentials: StoryEventCharacterSteps.When.UserQueries.PotentialWhens,
    private val characterRepository: CharacterRepository,
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : `Character Steps`.When.UserQueries.PotentialWhens {

    override fun the(character: Character.Id): `Character Steps`.When.UserQueries.PotentialWhens.PotentialActions {
        return object : `Character Steps`.When.UserQueries.PotentialWhens.PotentialActions {
            override fun `being removed from the`(project: Project.Id): PotentialChangesOfRemovingCharacterFromStory {
                val useCase = GetPotentialChangesOfRemovingCharacterFromStoryUseCase(characterRepository, storyEventRepository, sceneRepository)
                lateinit var result: PotentialChangesOfRemovingCharacterFromStory
                runBlocking {
                    useCase(character) { result = it }
                }
                return result
            }

            override fun `being removed from the`(storyEvent: StoryEvent.Id): PotentialChangesOfRemovingCharacterFromStoryEvent {
                return storyEventPotentials.characterRemovedFromStoryEvent(character, storyEvent)
            }
        }
    }

}