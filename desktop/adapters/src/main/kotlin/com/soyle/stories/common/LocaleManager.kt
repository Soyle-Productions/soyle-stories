package com.soyle.stories.common

import com.soyle.stories.scene.Locale

interface LocaleManager {

	suspend fun getCurrentLocale(): Locale

}