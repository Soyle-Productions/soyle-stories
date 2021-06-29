package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.`Create Opposition Value Dialog Access`.Companion.drive
import javafx.event.ActionEvent
import org.junit.jupiter.api.fail

fun getCreateOppositionValueDialogOrError(): CreateOppositionValueDialog =
    getCreateOppositionValueDialog() ?: fail("Create Opposition Value Dialog is not open")

fun getCreateOppositionValueDialog(): CreateOppositionValueDialog? = robot.getOpenDialog()

fun CreateOppositionValueDialog.createOppositionValueNamed(name: String)
{
    drive {
        nameInput.requestFocus()
        nameInput.text = name
        nameInput.fireEvent(ActionEvent())
    }
}