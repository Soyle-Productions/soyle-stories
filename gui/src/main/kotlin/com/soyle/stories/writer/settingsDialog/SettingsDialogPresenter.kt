package com.soyle.stories.writer.settingsDialog

import com.soyle.stories.gui.View
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferences

class SettingsDialogPresenter(
  private val view: View.Nullable<SettingsDialogViewModel>
) : GetAllDialogPreferences.OutputPort {

	override fun receiveAllDialogPreferences(response: GetAllDialogPreferences.ResponseModel) {
		view.update {
			SettingsDialogViewModel(
			  title = "Settings",
			  dialogSectionLabel = "Dialogs Enabled",
			  dialogs = response.dialogs.map {
				  DialogSettingViewModel(
					it.id.name,
					getDialogName(it.id),
					it.shouldShow
				  )
			  }
			)
		}
	}

	private fun getDialogName(dialogType: DialogType): String
	{
		return when (dialogType) {
			DialogType.DeleteScene -> "Confirm Delete Scene Dialog"
			DialogType.ReorderScene -> "Confirm Reorder Scene Dialog"
			DialogType.DeleteTheme -> "Confirm Delete Theme Dialog"
			DialogType.DeleteSymbol -> "Confirm Delete Symbol Dialog"
			DialogType.DeleteValueWeb -> "Confirm Delete Value Web Dialog"
		}
	}

}