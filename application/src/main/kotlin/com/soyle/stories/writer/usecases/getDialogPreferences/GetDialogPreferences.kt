package com.soyle.stories.writer.usecases.getDialogPreferences

import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference

interface GetDialogPreferences {

	suspend operator fun invoke(request: DialogType, output: OutputPort)

	interface OutputPort {
		fun failedToGetDialogPreferences(failure: Exception)
		fun gotDialogPreferences(response: DialogPreference)
	}

}