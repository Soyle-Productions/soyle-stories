package com.soyle.stories.theme.deleteThemeDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class DeleteThemeDialogModel : Model<ProjectScope, DeleteThemeDialogViewModel>(ProjectScope::class) {

    val title = bind(DeleteThemeDialogViewModel::title)
    val message = bind(DeleteThemeDialogViewModel::message)
    val doNotShowLabel = bind(DeleteThemeDialogViewModel::doNotShowLabel)
    val errorMessage = bind(DeleteThemeDialogViewModel::errorMessage)
    val deleteButtonLabel = bind(DeleteThemeDialogViewModel::deleteButtonLabel)
    val cancelButtonLabel = bind(DeleteThemeDialogViewModel::cancelButtonLabel)
    val doDefaultAction = bind(DeleteThemeDialogViewModel::doDefaultAction)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}