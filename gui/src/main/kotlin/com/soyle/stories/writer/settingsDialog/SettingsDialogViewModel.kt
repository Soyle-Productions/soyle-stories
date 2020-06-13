package com.soyle.stories.writer.settingsDialog

class SettingsDialogViewModel(
  val title: String,
  val dialogSectionLabel: String,
  val dialogs: List<DialogSettingViewModel>
)


data class DialogSettingViewModel(
  val dialogId: String,
  val label: String,
  val enabled: Boolean
)