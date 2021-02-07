package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.scene.CharacterNotInScene
import com.soyle.stories.domain.scene.SceneAlreadyContainsCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun sceneDoesNotExist(sceneId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneDoesNotExist
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for SceneDoesNotExist" }
}

fun characterNotInScene(sceneId: UUID, characterId: UUID): (Any?) -> Unit = { actual ->
	actual as CharacterNotInScene
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for CharacterNotInScene" }
	assertEquals(characterId, actual.characterId) { "Unexpected characterId for CharacterNotInScene" }
}

fun sceneAlreadyContainsCharacter(sceneId: UUID, characterId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneAlreadyContainsCharacter
	assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for SceneAlreadyContainsCharacter" }
	assertEquals(characterId, actual.characterId) { "Unexpected characterId for SceneAlreadyContainsCharacter" }
}
