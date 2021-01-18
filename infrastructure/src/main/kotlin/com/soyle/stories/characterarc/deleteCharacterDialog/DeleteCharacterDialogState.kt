package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope

class DeleteCharacterDialogState: Model<ProjectScope, DeleteCharacterDialogViewModel>(ProjectScope::class) {

    val title = bind(DeleteCharacterDialogViewModel::title)
    val message = bind(DeleteCharacterDialogViewModel::message)
    val deleteButtonLabel = bind(DeleteCharacterDialogViewModel::deleteButtonLabel)
    val cancelButtonLabel = bind(DeleteCharacterDialogViewModel::cancelButtonLabel)
    val doDefaultAction = bind(DeleteCharacterDialogViewModel::doDefaultAction)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}