package com.soyle.stories.di.writer

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesControllerImpl
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesNotifier
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferencesUseCase
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferencesUseCase

object WriterModule {

	init {
		scoped<ProjectScope> {

			provide<GetDialogPreferences> {
				GetDialogPreferencesUseCase(
				  applicationScope.writerId,
				  get()
				)
			}

			provide<SetDialogPreferences> {
				SetDialogPreferencesUseCase(
				  applicationScope.writerId,
				  get()
				)
			}

			provide(SetDialogPreferences.OutputPort::class) {
				SetDialogPreferencesNotifier()
			}

			provide<SetDialogPreferencesController> {
				SetDialogPreferencesControllerImpl(
				  applicationScope.get(),
				  get(),
				  get()
				)
			}

		}
	}

}