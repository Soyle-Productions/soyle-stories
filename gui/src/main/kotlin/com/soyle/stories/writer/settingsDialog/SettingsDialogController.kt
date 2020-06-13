package com.soyle.stories.writer.settingsDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferences

class SettingsDialogController(
  private val threadTransformer: ThreadTransformer,
  private val getAllDialogPreferences: GetAllDialogPreferences,
  private val getAllDialogPreferencesOutputPort: GetAllDialogPreferences.OutputPort,
  private val setDialogPreferencesController: SetDialogPreferencesController
) : SettingsDialogViewListener {


	override fun getValidState() {
		threadTransformer.async {
			getAllDialogPreferences.invoke(
			  getAllDialogPreferencesOutputPort
			)
		}
	}

	override fun saveDialogs(dialogs: List<Pair<String, Boolean>>) {
		dialogs.forEach {
			threadTransformer.async {
				setDialogPreferencesController.setDialogPreferences(it.first, it.second)
			}
		}
	}
}