package com.soyle.stories.core.definitions.character

import com.soyle.stories.core.framework.`Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.list.CharactersInScene

class `Character Assertions`(
    private val sceneCharacterSteps: `Scene Character Steps`.Then
) : `Character Steps`.Then {

    override fun the(character: Character.Id): `Character Steps`.Then.StateAssertions = object :
        `Character Steps`.Then.StateAssertions {
        override fun `in the`(scene: Scene.Id): `Scene Character Steps`.Then.CharacterInSceneStateAssertions {
            return sceneCharacterSteps.characterInScene(scene, character)
        }

        override fun `in the`(sceneCharacters: CharactersInScene): `Scene Character Steps`.Then.CharacterInSceneItemAssertions {
            return sceneCharacterSteps.characterInSceneItem(sceneCharacters, character)
        }
    }

}