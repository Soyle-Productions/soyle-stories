package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class CreateSymbolDialogModel : Model<ProjectScope, CreateSymbolDialogViewModel>(ProjectScope::class) {

    val title = bind(CreateSymbolDialogViewModel::title)
    val nameFieldLabel = bind(CreateSymbolDialogViewModel::nameFieldLabel)
    val errorMessage = bind(CreateSymbolDialogViewModel::errorMessage)
    val created = bind(CreateSymbolDialogViewModel::created)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}