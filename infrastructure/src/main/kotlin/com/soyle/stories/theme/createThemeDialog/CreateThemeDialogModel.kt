package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class CreateThemeDialogModel : Model<ProjectScope, CreateThemeDialogViewModel>(ProjectScope::class) {

    val title = bind(CreateThemeDialogViewModel::title)
    val nameFieldLabel = bind(CreateThemeDialogViewModel::nameFieldLabel)
    val errorMessage = bind(CreateThemeDialogViewModel::errorMessage)
    val created = bind(CreateThemeDialogViewModel::created)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}