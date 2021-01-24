package com.soyle.stories.di.modules

import com.soyle.stories.common.AsyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.scoped
import com.soyle.stories.soylestories.ApplicationScope

object ApplicationModule {

	init {

		scoped<ApplicationScope> {
			provide<ThreadTransformer> {
				AsyncThreadTransformer(this)
			}
		}

	}

}