package com.soyle.stories.di.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.locationDetails.*

object LocationDetailsModule {
	init {
		scoped<LocationDetailsScope> {

			provide<LocationDetailsViewListener> {
				LocationDetailsController(
				  projectScope.applicationScope.get(),
				  locationId,
				  projectScope.get(),
				  LocationDetailsPresenter(
					get<LocationDetailsModel>()
				  )
				)
			}

		}
	}
}