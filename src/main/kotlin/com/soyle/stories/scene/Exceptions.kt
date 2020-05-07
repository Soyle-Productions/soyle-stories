package com.soyle.stories.scene

interface SceneException
class SceneNameCannotBeBlank(private val locale: Locale) : IllegalArgumentException(), SceneException {
	override fun getLocalizedMessage(): String = locale.sceneNameCannotBeBlank
}