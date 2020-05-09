package com.soyle.stories.gui

import com.soyle.stories.scene.Locale

interface LocaleManager {

	suspend fun getCurrentLocale(): Locale

}