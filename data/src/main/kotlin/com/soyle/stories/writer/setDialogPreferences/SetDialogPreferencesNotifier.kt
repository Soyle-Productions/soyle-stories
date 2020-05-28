package com.soyle.stories.writer.setDialogPreferences

import com.soyle.stories.common.Notifier
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences

class SetDialogPreferencesNotifier : Notifier<SetDialogPreferences.OutputPort>(), SetDialogPreferences.OutputPort {
	override fun dialogPreferenceSet(response: SetDialogPreferences.ResponseModel) {
		notifyAll { it.dialogPreferenceSet(response) }
	}

	override fun failedToSetDialogPreferences(failure: Exception) {
		notifyAll { it.failedToSetDialogPreferences(failure) }
	}
}