package com.soyle.stories.scene

abstract class SceneException : Exception()
class SceneNameCannotBeBlank(private val locale: Locale) : SceneException() {
	override fun getLocalizedMessage(): String = locale.sceneNameCannotBeBlank
}