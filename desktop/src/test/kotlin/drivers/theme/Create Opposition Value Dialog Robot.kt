package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.`Create Opposition Value Form Access`.Companion.drive
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.getOpenCreateOppositionValueDialog
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import javafx.event.ActionEvent
import org.junit.jupiter.api.fail

fun getCreateOppositionValueDialogOrError(): CreateOppositionValueForm =
    robot.getOpenCreateOppositionValueDialog() ?: fail("Create Opposition Value Dialog is not open")

fun CreateOppositionValueForm.createOppositionValueNamed(name: String)
{
    drive {
        nameInput.requestFocus()
        nameInput.text = name
        nameInput.fireEvent(ActionEvent())
    }
}