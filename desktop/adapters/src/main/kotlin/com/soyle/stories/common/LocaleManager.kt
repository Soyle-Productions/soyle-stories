package com.soyle.stories.common

import com.soyle.stories.domain.scene.SceneLocale

@Deprecated("Will be replaced with more explicit Locale management in the future")
interface LocaleManager {

	suspend fun getCurrentLocale(): SceneLocale

}