package com.soyle.stories.desktop.view.theme.valueWeb.create

import com.soyle.stories.common.exists
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import javafx.scene.control.Label
import javafx.scene.control.TextInputControl
import tornadofx.Stylesheet
import tornadofx.hasClass

class `Create Value Web Form Access`(val form: CreateValueWebForm) : NodeAccess<CreateValueWebForm>(form) {
    companion object :
        NodeAccess.Factory<CreateValueWebForm, CreateValueWebForm, `Create Value Web Form Access`>(::`Create Value Web Form Access`)

    val nameLabel: Label by mandatoryChild(Stylesheet.label) { !it.hasClass(Stylesheet.error) }
    val nameInput: TextInputControl by mandatoryChild(Stylesheet.textInput)
    val errorMessage: Label? by temporaryChild(Stylesheet.error) { it.exists }
}