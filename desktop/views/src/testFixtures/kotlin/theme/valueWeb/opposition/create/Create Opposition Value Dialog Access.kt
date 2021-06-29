package com.soyle.stories.desktop.view.theme.valueWeb.opposition.create

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import javafx.scene.Parent
import javafx.scene.control.TextInputControl
import tornadofx.Stylesheet

class `Create Opposition Value Dialog Access`(val dialog: CreateOppositionValueDialog) :
    NodeAccess<Parent>(dialog.root) {

    companion object :
        NodeAccess.Factory<CreateOppositionValueDialog, Parent, `Create Opposition Value Dialog Access`>(::`Create Opposition Value Dialog Access`)

    val nameInput: TextInputControl by mandatoryChild(Stylesheet.textInput)
}