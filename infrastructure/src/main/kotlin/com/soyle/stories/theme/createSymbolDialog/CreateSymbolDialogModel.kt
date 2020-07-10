package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.onChange

class CreateSymbolDialogModel : Model<ProjectScope, CreateSymbolDialogViewModel>(ProjectScope::class) {

    val title = bind(CreateSymbolDialogViewModel::title)
    val nameFieldLabel = bind(CreateSymbolDialogViewModel::nameFieldLabel)
    val errorMessage = bind(CreateSymbolDialogViewModel::errorMessage)
    val errorCause = bind(CreateSymbolDialogViewModel::errorCause)
    val themes = bindImmutableList(CreateSymbolDialogViewModel::themes)
    val createdId = bind(CreateSymbolDialogViewModel::createdId)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

}