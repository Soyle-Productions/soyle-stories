package com.soyle.stories.scene

import java.util.*

abstract class SceneException : Exception()
class SceneNameCannotBeBlank(private val locale: Locale) : SceneException() {
	override fun getLocalizedMessage(): String = locale.sceneNameCannotBeBlank
}
class SceneDoesNotExist(private val locale: Locale, val sceneId: UUID): SceneException() {
	override fun getLocalizedMessage(): String = locale.sceneDoesNotExist
}
class NoSceneExistsWithStoryEventId(val storyEventId: UUID) : SceneException()