package com.soyle.stories.di.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.locationList.LocationListController
import com.soyle.stories.location.locationList.LocationListModel
import com.soyle.stories.location.locationList.LocationListPresenter
import com.soyle.stories.location.locationList.LocationListViewListener
import com.soyle.stories.project.ProjectScope

internal object LocationListModule {

	init {
		scoped<ProjectScope> {
			provide<LocationListViewListener> {
				LocationListController(
				  applicationScope.get(),
				  get(),
				  LocationListPresenter(
					get<LocationListModel>(),
					get()
				  ),
				  get(),
				  get()
				)
			}
		}
	}

}