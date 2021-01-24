package com.soyle.stories.theme.createOppositionValueDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class CreateOppositionValueDialogModel : Model<ProjectScope, CreateOppositionValueDialogViewModel>(ProjectScope::class) {

    val title = bind(CreateOppositionValueDialogViewModel::title)
    val nameFieldLabel = bind(CreateOppositionValueDialogViewModel::nameFieldLabel)
    val errorMessage = bind(CreateOppositionValueDialogViewModel::errorMessage)
    val created = bind(CreateOppositionValueDialogViewModel::created)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}