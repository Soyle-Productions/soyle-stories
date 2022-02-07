package com.soyle.stories.di.modules

import com.soyle.stories.di.scoped
import com.soyle.stories.soylestories.ApplicationScope

object LocaleModule {

	init {

		scoped<ApplicationScope> {
		}
	}
}