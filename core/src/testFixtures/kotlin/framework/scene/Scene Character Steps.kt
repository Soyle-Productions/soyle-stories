package com.soyle.stories.core.framework.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene

interface `Scene Character Steps` {

    interface Given {

        fun characterIncludedInScene(scene: Scene.Id, character: Character.Id)
        fun characterRemovedFromScene(scene: Scene.Id, character: Character.Id)
        fun characterInScene(sceneId: Scene.Id, characterId: Character.Id): CharacterInSceneExpectations
        interface CharacterInSceneExpectations {
            infix fun `has been assigned to be the`(role: RoleInScene)
            fun `has been motivated by`(motivation: String)
        }
    }

    interface When {

        fun includeCharacterInScene(scene: Scene.Id, character: Character.Id)
        fun removeCharacterFromScene(scene: Scene.Id, character: Character.Id)
        fun characterInScene(scene: Scene.Id, character: Character.Id): CharacterInSceneActions
        interface CharacterInSceneActions {
            infix fun `is assigned to be the`(role: RoleInScene)
            infix fun desires(desire: String)
            infix fun `is motivated by`(motivation: String)
        }

        interface UserQueries {
            infix fun `lists the characters in the`(scene: Scene.Id): CharactersInScene
            infix fun `lists the available characters to include in the`(scene: Scene.Id): AvailableCharactersToAddToScene

            fun `inspects the`(character: Character.Id, inThe: Scene.Id): CharacterInSceneInspection
        }

    }

    interface Then {

        infix fun the(charactersInScene: CharactersInScene): SceneCharactersStateAssertions
        interface SceneCharactersStateAssertions {
            infix fun `should include the`(character: Character.Id)
            infix fun `should not include the`(character: Character.Id)
        }

        fun characterInScene(sceneId: Scene.Id, character: Character.Id): CharacterInSceneStateAssertions
        interface CharacterInSceneStateAssertions {
            infix fun `should be the`(role: RoleInScene)
            infix fun `should not have a role`(stop: Unit)
            infix fun `should have the desire of`(desire: String)
            infix fun `should be motivated by`(motivation: String)
        }

        fun characterInSceneItem(charactersInScene: CharactersInScene, character: Character.Id): CharacterInSceneItemAssertions
        interface CharacterInSceneItemAssertions {
            fun `should not have any story event coverage`()
            fun `should be flagged as removed from the story`()
            infix fun `should have the role`(role: RoleInScene)
        }

        infix fun the(availableCharactersToAddToScene: AvailableCharactersToAddToScene): AvailableCharactersToAddToSceneAssertions
        interface AvailableCharactersToAddToSceneAssertions {
            fun `should not include any characters`()
            infix fun `should include the`(characterId: Character.Id)
        }

        fun the(inspection: CharacterInSceneInspection): CharacterInSceneInspectionAssertions
        interface CharacterInSceneInspectionAssertions {
            fun `should not have an inherited motivation`()
            fun `should have an inherited motivation of`(motivation: String)
            fun `should have an inherited motivation from the`(scene: Scene.Id)
            fun `should have motivation of`(motivation: String)
        }

    }

    interface PotentialEffectBuilder {

        fun the(character: Character.Id): SceneCharacterEffectBuilder
        interface SceneCharacterEffectBuilder {
            fun `will be removed from the`(scene: Scene.Id): ImplicitCharacterRemovedFromScene
        }
    }

}