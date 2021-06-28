package com.soyle.stories.common

import com.soyle.stories.domain.scene.SceneLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornadofx.FX

class LocaleManagerImpl : LocaleManager {

	private var localeLoader = lazy {
		val localeType = FX.locale
		val localeCode = "${localeType.language}_${localeType.country}"
		this::class.java.classLoader.loadClass("lang.$localeCode.Scenes_$localeCode")
		  .constructors.first().newInstance() as SceneLocale
	}

	override suspend fun getCurrentLocale(): SceneLocale = withContext(Dispatchers.IO) {
		localeLoader.value
	}

}