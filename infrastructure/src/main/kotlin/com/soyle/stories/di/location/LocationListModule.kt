package com.soyle.stories.di.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.DeleteLocationNotifier
import com.soyle.stories.location.events.RenameLocationNotifier
import com.soyle.stories.location.locationList.*
import com.soyle.stories.project.ProjectScope

internal object LocationListModule {

	init {
		scoped<ProjectScope> {

			provide {
				LiveLocationList(
				  applicationScope.get(),
				  get(),
				  get<CreateNewLocationNotifier>(),
				  get<DeleteLocationNotifier>(),
				  get<RenameLocationNotifier>()
				)
			}

			provide<LocationListViewListener> {

				LocationListController(
				  applicationScope.get(),
				  get(),
				  LocationListPresenter(
					get<LocationListModel>()
				  ),
				  get(),
				  get()
				)
			}
		}
	}

}