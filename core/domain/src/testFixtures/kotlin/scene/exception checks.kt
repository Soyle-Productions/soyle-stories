package com.soyle.stories.domain.scene

import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*


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
