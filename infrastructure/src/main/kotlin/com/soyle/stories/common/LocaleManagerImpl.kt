package com.soyle.stories.common

import com.soyle.stories.gui.LocaleManager
import com.soyle.stories.scene.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornadofx.FX

class LocaleManagerImpl : LocaleManager {

	private var localeLoader = lazy {
		val localeType = FX.locale
		val localeCode = "${localeType.language}_${localeType.country}"
		this::class.java.classLoader.loadClass("lang.$localeCode.Scenes_$localeCode")
		  .constructors.first().newInstance() as Locale
	}

	override suspend fun getCurrentLocale(): Locale = withContext(Dispatchers.IO) {
		localeLoader.value
	}

}