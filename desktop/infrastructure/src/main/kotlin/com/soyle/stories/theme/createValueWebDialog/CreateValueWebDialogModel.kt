package com.soyle.stories.theme.createValueWebDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class CreateValueWebDialogModel : Model<ProjectScope, CreateValueWebDialogViewModel>(ProjectScope::class) {

    val title = bind(CreateValueWebDialogViewModel::title)
    val nameFieldLabel = bind(CreateValueWebDialogViewModel::nameFieldLabel)
    val errorMessage = bind(CreateValueWebDialogViewModel::errorMessage)
    val created = bind(CreateValueWebDialogViewModel::created)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}