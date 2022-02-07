@file:Suppress("FunctionName")

package com.soyle.stories.core.framework.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import com.soyle.stories.usecase.storyevent.remove.PotentialChangesOfRemovingStoryEventFromProject

interface `Story Event Steps` {

    interface Given {

        fun `a story event`(coveredBy: Scene.Id): ExistenceExpectations
        fun `a story event`(named: String = storyEventName().value): ExistenceExpectations
        interface ExistenceExpectations {
            infix fun `has been created in the`(project: Project.Id): StoryEvent.Id
        }

        infix fun the(storyEventId: StoryEvent.Id): StateExpectations
        interface StateExpectations {
            infix fun `has been covered by the`(sceneId: Scene.Id)
            fun `has been uncovered`()
            fun `has been removed from the story`()
        }

    }

    interface When {

        fun `a story event`(
            named: String = storyEventName().value,
            atTime: Long = 0,
            coveredBy: Scene.Id? = null
        ): CreationActions
        interface CreationActions {
            fun `is created in the`(project: Project.Id): StoryEvent.Id
        }

        infix fun the(storyEvent: StoryEvent.Id): StoryEventActions
        interface StoryEventActions {
            infix fun `is covered by the`(scene: Scene.Id)
            fun `is uncovered`()
            fun `is removed`()
        }

        interface UserQueries {
            infix fun `lists the available characters to involve in the`(
                storyEventId: StoryEvent.Id
            ): AvailableCharactersToInvolveInStoryEvent

            fun `lists the potential changes of`(): PotentialWhens
            interface PotentialWhens : StoryEventCharacterSteps.When.UserQueries.PotentialWhens {

                fun the(storyEvent: StoryEvent.Id): PotentialActions
                interface PotentialActions {
                    fun `being uncovered`(): PotentialChangesFromUncoveringStoryEvent
                    fun `being removed from the`(project: Project.Id): PotentialChangesOfRemovingStoryEventFromProject
                }
            }
        }
    }

    interface Then {

        fun `a story event`(named: String): ExistenceAssertions
        interface ExistenceAssertions {

            fun `should have been created in`(project: Project.Id): StoryEvent.Id
            fun `should not exist`(inProject: Project.Id)
        }

        infix fun the(storyEventId: StoryEvent.Id): StateAssertions
        interface StateAssertions {
            infix fun `should be at time`(time: Long)
            infix fun `should be covered by the`(scene: Scene.Id)
            infix fun `should not be covered by the`(scene: Scene.Id)
            fun `should not involve any characters`()
            infix fun `should involve the`(character: Character.Id)
        }

        infix fun the(availableCharacters: AvailableCharactersToInvolveInStoryEvent): AvailableCharactersStateAssertions
        interface AvailableCharactersStateAssertions {
            infix fun `should have an item for the`(characterId: Character.Id)
            infix fun `should not have an item for the`(characterId: Character.Id)
        }

    }

}