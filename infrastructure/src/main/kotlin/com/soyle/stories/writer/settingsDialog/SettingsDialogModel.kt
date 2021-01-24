package com.soyle.stories.writer.settingsDialog

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleListProperty

class SettingsDialogModel : Model<ProjectScope, SettingsDialogViewModel>(ProjectScope::class) {

	val title = bind(SettingsDialogViewModel::title)
	val dialogSectionLabel = bind(SettingsDialogViewModel::dialogSectionLabel)
	val dialogs = bind(SettingsDialogViewModel::dialogs)
	val dialogUpdates = SimpleListProperty<DialogSettingViewModel>()

	override val applicationScope: ApplicationScope
		get() = scope.applicationScope

}