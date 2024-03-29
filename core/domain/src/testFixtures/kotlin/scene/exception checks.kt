package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*


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
