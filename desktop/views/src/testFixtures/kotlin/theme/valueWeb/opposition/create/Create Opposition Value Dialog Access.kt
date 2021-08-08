package com.soyle.stories.desktop.view.theme.valueWeb.opposition.create

import com.soyle.stories.common.exists
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot
import tornadofx.Stylesheet
import tornadofx.hasClass

class `Create Opposition Value Form Access`(val dialog: CreateOppositionValueForm) :
    NodeAccess<CreateOppositionValueForm>(dialog) {

    companion object :
        NodeAccess.Factory<CreateOppositionValueForm, CreateOppositionValueForm, `Create Opposition Value Form Access`>(::`Create Opposition Value Form Access`)

    val nameLabel: Label by mandatoryChild(Stylesheet.label) { ! it.hasClass(Stylesheet.error) }
    val nameInput: TextInputControl by mandatoryChild(Stylesheet.textInput)
    val errorMessage: Label? by temporaryChild(Stylesheet.error) { it.isVisible }
}

fun FxRobot.getOpenCreateOppositionValueDialog(): CreateOppositionValueForm? =
    listWindows().asSequence()
        .mapNotNull { it.scene.root as? CreateOppositionValueForm }
        .firstOrNull { it.scene.window?.isShowing == true }