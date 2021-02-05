package com.soyle.stories.writer.setDialogPreferences

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences

class SetDialogPreferencesControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val setDialogPreferences: SetDialogPreferences,
  private val setDialogPreferencesOutputPort: SetDialogPreferences.OutputPort
) : SetDialogPreferencesController {
	override fun setDialogPreferences(dialog: String, shouldShow: Boolean) {
		val request = DialogType.valueOf(dialog)
		threadTransformer.async {
			setDialogPreferences.invoke(
			  request, shouldShow, setDialogPreferencesOutputPort
			)
		}
	}
}