package com.soyle.stories.scene.usecases

import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneNameCannotBeBlank

fun validateSceneName(name: String, locale: Locale) {
	if (name.isBlank()) throw SceneNameCannotBeBlank(locale)
}