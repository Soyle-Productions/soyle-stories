package com.soyle.stories.writer.settingsDialog

interface SettingsDialogViewListener {

	fun getValidState()
	fun saveDialogs(dialogs: List<Pair<String, Boolean>>)

}