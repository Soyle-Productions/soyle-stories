package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class DeleteSceneDialogModel : Model<ProjectScope, DeleteSceneDialogViewModel>(ProjectScope::class) {


	val title = bind(DeleteSceneDialogViewModel::title)
	val header = bind(DeleteSceneDialogViewModel::header)
	val content = bind(DeleteSceneDialogViewModel::content)
	val deleteButtonLabel = bind(DeleteSceneDialogViewModel::deleteButtonLabel)
	val cancelButtonLabel = bind(DeleteSceneDialogViewModel::cancelButtonLabel)
	val errorMessage = bind(DeleteSceneDialogViewModel::errorMessage)
	val defaultAction = bind(DeleteSceneDialogViewModel::defaultAction)



	override val applicationScope: ApplicationScope
		get() = scope.applicationScope

}