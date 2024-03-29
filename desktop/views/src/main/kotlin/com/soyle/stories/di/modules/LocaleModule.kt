package com.soyle.stories.di.modules

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.LocaleManagerImpl
import com.soyle.stories.di.scoped
import com.soyle.stories.soylestories.ApplicationScope

object LocaleModule {

	init {

		scoped<ApplicationScope> {
			provide<LocaleManager> {
				LocaleManagerImpl()
			}
		}
	}
}