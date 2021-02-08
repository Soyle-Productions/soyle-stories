package com.soyle.stories.common

import com.soyle.stories.domain.scene.SceneLocale

interface LocaleManager {

	suspend fun getCurrentLocale(): SceneLocale

}