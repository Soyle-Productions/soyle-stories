package com.soyle.stories.writer.setDialogPreferences

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences

class SetDialogPreferencesNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<SetDialogPreferences.OutputPort>(), SetDialogPreferences.OutputPort {
	override fun dialogPreferenceSet(response: SetDialogPreferences.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.dialogPreferenceSet(response) }
		}
	}

	override fun failedToSetDialogPreferences(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToSetDialogPreferences(failure) }
		}
	}
}