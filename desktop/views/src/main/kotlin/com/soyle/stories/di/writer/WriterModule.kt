package com.soyle.stories.di.writer

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesControllerImpl
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesNotifier
import com.soyle.stories.writer.settingsDialog.SettingsDialogController
import com.soyle.stories.writer.settingsDialog.SettingsDialogModel
import com.soyle.stories.writer.settingsDialog.SettingsDialogPresenter
import com.soyle.stories.writer.settingsDialog.SettingsDialogViewListener
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferences
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferencesUseCase
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
			provide<GetAllDialogPreferences> {
				GetAllDialogPreferencesUseCase(
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
				SetDialogPreferencesNotifier(applicationScope.get())
			}

			provide<SetDialogPreferencesController> {
				SetDialogPreferencesControllerImpl(
				  applicationScope.get(),
				  get(),
				  get()
				)
			}
			provide<SettingsDialogViewListener> {
				SettingsDialogController(
				  applicationScope.get(),
				  get(),
				  SettingsDialogPresenter(
					get<SettingsDialogModel>()
				  ),
				  get()
				)
			}

		}
	}

}