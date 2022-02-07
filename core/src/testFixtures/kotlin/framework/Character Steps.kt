package com.soyle.stories.core.framework

import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent

interface `Character Steps` {

    interface Given {

        fun `a character`(named: String = characterName().value): CreationExpectations
        interface CreationExpectations {
            infix fun `has been created in the`(project: Project.Id): Character.Id
        }

        infix fun the(characterId: Character.Id): StateExpectations
        interface StateExpectations {
            infix fun `has been involved in the`(storyEventId: StoryEvent.Id)
            infix fun `has been removed from the`(storyEvent: StoryEvent.Id)
            infix fun `has been removed from the`(project: Project.Id)
            infix fun `has been included in the`(scene: Scene.Id)
            infix fun `has been removed from the`(scene: Scene.Id)
            infix fun `in the`(scene: Scene.Id): `Scene Character Steps`.Given.CharacterInSceneExpectations
        }

    }

    interface When {

        fun `a character`(named: String = characterName().value): CreationActions
        interface CreationActions {
            infix fun `is created in the`(project: Project.Id): Character.Id
        }

        infix fun the(character: Character.Id): CharacterActions
        interface CharacterActions {
            infix fun `is involved in the`(storyEvent: StoryEvent.Id)
            fun `is involved in the`(vararg storyEvent: StoryEvent.Id, and: StoryEvent.Id)
            infix fun `is no longer involved in the`(storyEventId: StoryEvent.Id)
            infix fun `is removed from the`(projectId: Project.Id)
            infix fun `is included in the`(scene: Scene.Id)
            infix fun `in the`(scene: Scene.Id): `Scene Character Steps`.When.CharacterInSceneActions
        }

        interface UserQueries {

            fun `lists the potential changes of`(): PotentialWhens
            interface PotentialWhens {

                fun the(character: Character.Id): PotentialActions
                interface PotentialActions {
                    fun `being removed from the`(project: Project.Id): PotentialChangesOfRemovingCharacterFromStory
                    fun `being removed from the`(storyEvent: StoryEvent.Id): PotentialChangesOfRemovingCharacterFromStoryEvent
                }
            }
        }

    }

    interface Then {

        infix fun the(character: Character.Id): StateAssertions
        interface StateAssertions {
            infix fun `in the`(scene: Scene.Id): `Scene Character Steps`.Then.CharacterInSceneStateAssertions
            infix fun `in the`(sceneCharacters: CharactersInScene): `Scene Character Steps`.Then.CharacterInSceneItemAssertions
        }


    }
}