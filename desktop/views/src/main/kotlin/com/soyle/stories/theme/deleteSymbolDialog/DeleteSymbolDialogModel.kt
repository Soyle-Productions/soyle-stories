package com.soyle.stories.theme.deleteSymbolDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialogViewModel

class DeleteSymbolDialogModel : Model<ProjectScope, DeleteSymbolDialogViewModel>(ProjectScope::class) {

    val title = bind(DeleteSymbolDialogViewModel::title)
    val message = bind(DeleteSymbolDialogViewModel::message)
    val doNotShowLabel = bind(DeleteSymbolDialogViewModel::doNotShowLabel)
    val errorMessage = bind(DeleteSymbolDialogViewModel::errorMessage)
    val deleteButtonLabel = bind(DeleteSymbolDialogViewModel::deleteButtonLabel)
    val cancelButtonLabel = bind(DeleteSymbolDialogViewModel::cancelButtonLabel)
    val doDefaultAction = bind(DeleteSymbolDialogViewModel::doDefaultAction)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}