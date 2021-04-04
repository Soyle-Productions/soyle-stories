package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.SceneAlreadyContainsCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun sceneDoesNotExist(sceneId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneDoesNotExist
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for SceneDoesNotExist" }
}

fun characterNotInScene(sceneId: Scene.Id, characterId: Character.Id): (Any?) -> Unit = { actual ->
	actual as SceneDoesNotIncludeCharacter
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for CharacterNotInScene" }
	assertEquals(characterId, actual.characterId) { "Unexpected characterId for CharacterNotInScene" }
}

fun sceneAlreadyContainsCharacter(sceneId: UUID, characterId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneAlreadyContainsCharacter
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for SceneAlreadyContainsCharacter" }
	assertEquals(characterId, actual.characterId) { "Unexpected characterId for SceneAlreadyContainsCharacter" }
}
