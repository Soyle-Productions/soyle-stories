package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.createValueWebDialog.CreateValueWebDialogDriver
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebDriver
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.desktop.view.theme.valueWeb.create.`Create Value Web Form Access`.Companion.drive
import com.soyle.stories.desktop.view.theme.valueWeb.create.getOpenCreateValueWebDialog
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun getCreateValueWebDialog(): CreateValueWebForm? =
    robot.getOpenCreateValueWebDialog()

fun ValueOppositionWebs.openCreateValueWebDialog(): CreateValueWebForm
{
    val driver = ValueOppositionWebDriver(this)
    val createButton = driver.getCreateValueWebButton()
    driver.interact { createButton.fire() }
    return getCreateValueWebDialog() ?: error("Value Opposition Web tool did not properly open Create Value Web Dialog")
}

fun CreateValueWebForm.createValueWebNamed(valueWebName: String)
{
    drive {
        nameInput.text = valueWebName
        nameInput.fireEvent(ActionEvent())
    }
}