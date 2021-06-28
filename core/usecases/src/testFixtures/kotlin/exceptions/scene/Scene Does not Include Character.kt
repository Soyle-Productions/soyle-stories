package com.soyle.stories.usecase.exceptions.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows

fun assertThrowsSceneDoesNotIncludeCharacter(sceneId: Scene.Id, characterId: Character.Id, test: () -> Unit) {
    val error = assertThrows<SceneDoesNotIncludeCharacter> {
        test()
    }
    Assertions.assertEquals(sceneId, error.sceneId) { "Scene Does Not Include Character error has wrong scene id" }
    Assertions.assertEquals(characterId, error.characterId) { "Scene Does Not Include Character error has wrong character id" }
}