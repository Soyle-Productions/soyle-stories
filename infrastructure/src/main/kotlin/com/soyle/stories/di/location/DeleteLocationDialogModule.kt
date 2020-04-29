package com.soyle.stories.di.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogController
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogModel
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogPresenter
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogViewListener
import com.soyle.stories.project.ProjectScope

internal object DeleteLocationDialogModule {

	init {

		scoped<ProjectScope> {
			provide<DeleteLocationDialogViewListener> {
				DeleteLocationDialogController(
				  applicationScope.get(),
				  get(),
				  DeleteLocationDialogPresenter(
					get<DeleteLocationDialogModel>()
				  )
				)
			}
		}

	}
}