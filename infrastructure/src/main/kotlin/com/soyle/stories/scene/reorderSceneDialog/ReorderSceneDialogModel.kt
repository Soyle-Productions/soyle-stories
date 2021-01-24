package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class ReorderSceneDialogModel : Model<ProjectScope, ReorderSceneDialogViewModel>(ProjectScope::class) {

	val title = bind(ReorderSceneDialogViewModel::title)
	val header = bind(ReorderSceneDialogViewModel::header)
	val content = bind(ReorderSceneDialogViewModel::content)
	val reorderButtonLabel = bind(ReorderSceneDialogViewModel::reorderButtonLabel)
	val cancelButtonLabel = bind(ReorderSceneDialogViewModel::cancelButtonLabel)
	val showAgainLabel = bind(ReorderSceneDialogViewModel::showAgainLabel)
	val showAgain = bind(ReorderSceneDialogViewModel::showAgain)
	val errorMessage = bind(ReorderSceneDialogViewModel::errorMessage)

	override val applicationScope: ApplicationScope
		get() = scope.applicationScope
}