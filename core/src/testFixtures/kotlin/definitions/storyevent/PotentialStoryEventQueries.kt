package com.soyle.stories.core.definitions.storyevent

import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.remove.GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.GetPotentialChangesFromUncoveringStoryEventUseCase
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.remove.GetPotentialChangesOfRemovingStoryEventFromProjectUseCase
import com.soyle.stories.usecase.storyevent.remove.PotentialChangesOfRemovingStoryEventFromProject
import kotlinx.coroutines.runBlocking

class PotentialStoryEventQueries(
    private val characterRepository: CharacterRepository,
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : `Story Event Steps`.When.UserQueries.PotentialWhens {
    override fun the(storyEvent: StoryEvent.Id): `Story Event Steps`.When.UserQueries.PotentialWhens.PotentialActions {
        return object : `Story Event Steps`.When.UserQueries.PotentialWhens.PotentialActions {
            override fun `being uncovered`(): PotentialChangesFromUncoveringStoryEvent {
                val useCase = GetPotentialChangesFromUncoveringStoryEventUseCase(
                    storyEventRepository,
                    sceneRepository,
                    characterRepository
                )
                lateinit var result: PotentialChangesFromUncoveringStoryEvent
                runBlocking {
                    useCase(storyEvent) { result = it }
                }
                return result
            }

            override fun `being removed from the`(project: Project.Id): PotentialChangesOfRemovingStoryEventFromProject {
                val useCase = GetPotentialChangesOfRemovingStoryEventFromProjectUseCase(
                    storyEventRepository,
                    sceneRepository,
                    characterRepository
                )
                lateinit var result: PotentialChangesOfRemovingStoryEventFromProject
                runBlocking {
                    useCase(storyEvent) { result = it }
                }
                return result
            }
        }
    }

    override fun characterRemovedFromStoryEvent(
        character: Character.Id,
        storyEvent: StoryEvent.Id
    ): PotentialChangesOfRemovingCharacterFromStoryEvent {
        val useCase = GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase(
            storyEventRepository,
            characterRepository,
            sceneRepository,
        )
        lateinit var result: PotentialChangesOfRemovingCharacterFromStoryEvent
        runBlocking {
            useCase(storyEvent, character) { result = it }
        }
        return result
    }
}