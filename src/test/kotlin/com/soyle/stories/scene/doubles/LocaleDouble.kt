package com.soyle.stories.scene.doubles

import com.soyle.stories.scene.Locale

class LocaleDouble : Locale {
	override val sceneDoesNotExist: String = "Scene Does Not Exist"
	override val sceneNameCannotBeBlank: String = "Scene Name Cannot Be Blank"
}