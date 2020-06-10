package com.soyle.stories.scene

import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun sceneDoesNotExist(sceneId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneDoesNotExist
	assertEquals(sceneId, actual.sceneId)
}

fun characterNotInScene(sceneId: UUID, characterId: UUID): (Any?) -> Unit = { actual ->
	actual as CharacterNotInScene
	assertEquals(sceneId, actual.sceneId)
}