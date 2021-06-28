package com.soyle.stories.writer.usecases.setDialogPreferences

import com.soyle.stories.writer.DialogType

interface SetDialogPreferences {

	suspend operator fun invoke(dialog: DialogType, preference: Boolean, outputPort: OutputPort)

	class ResponseModel(val dialog: String, val preference: Boolean)

	interface OutputPort {
		fun failedToSetDialogPreferences(failure: Exception)
		fun dialogPreferenceSet(response: ResponseModel)
	}
}