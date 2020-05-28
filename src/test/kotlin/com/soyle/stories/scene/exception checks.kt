package com.soyle.stories.scene

import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun sceneDoesNotExist(sceneId: UUID): (Any?) -> Unit = { actual ->
	actual as SceneDoesNotExist
	assertEquals(sceneId, actual.sceneId)
}