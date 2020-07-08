package com.soyle.stories.theme.deleteValueWebDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.deleteSymbolDialog.DeleteSymbolDialogViewModel
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialogViewModel

class DeleteValueWebDialogModel : Model<ProjectScope, DeleteValueWebDialogViewModel>(ProjectScope::class) {

    val title = bind(DeleteValueWebDialogViewModel::title)
    val message = bind(DeleteValueWebDialogViewModel::message)
    val doNotShowLabel = bind(DeleteValueWebDialogViewModel::doNotShowLabel)
    val errorMessage = bind(DeleteValueWebDialogViewModel::errorMessage)
    val deleteButtonLabel = bind(DeleteValueWebDialogViewModel::deleteButtonLabel)
    val cancelButtonLabel = bind(DeleteValueWebDialogViewModel::cancelButtonLabel)
    val doDefaultAction = bind(DeleteValueWebDialogViewModel::doDefaultAction)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}