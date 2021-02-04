package com.soyle.stories.writer.usecases.getAllDialogPreferences

import com.soyle.stories.writer.usecases.DialogPreference

interface GetAllDialogPreferences {

	suspend operator fun invoke(output: OutputPort)

	class ResponseModel(val dialogs: List<DialogPreference>)



	interface OutputPort {
		fun receiveAllDialogPreferences(response: ResponseModel)
	}

}