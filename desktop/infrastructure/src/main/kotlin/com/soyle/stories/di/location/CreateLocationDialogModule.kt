package com.soyle.stories.di.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogModel
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogPresenter
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewListener
import com.soyle.stories.project.ProjectScope

internal object CreateLocationDialogModule {

	init {

		scoped<ProjectScope> {

			provide<CreateLocationDialogViewListener> {

				val createLocationDialogPresenter by lazy {
					CreateLocationDialogPresenter(
					  get<CreateLocationDialogModel>(),
					  get()
					)
				}

				CreateLocationDialogController(
				  applicationScope.get(),
				  get(),
				  createLocationDialogPresenter
				)
			}

		}

	}
}