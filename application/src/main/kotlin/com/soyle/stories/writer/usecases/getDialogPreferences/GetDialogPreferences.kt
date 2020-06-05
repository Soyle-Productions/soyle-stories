package com.soyle.stories.writer.usecases.getDialogPreferences

import com.soyle.stories.writer.DialogType

interface GetDialogPreferences {

	suspend operator fun invoke(request: DialogType, output: OutputPort)

	class ResponseModel(val dialog: String, val shouldShow: Boolean)

	interface OutputPort {
		fun failedToGetDialogPreferences(failure: Exception)
		fun gotDialogPreferences(response: ResponseModel)
	}

}