package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.character.createArcSectionDialog.drive
import com.soyle.stories.desktop.view.character.createArcSectionDialog.driver
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog

fun getCreateArcSectionDialogOrError(): CreateArcSectionDialogView =
    getCreateArcSectionDialog() ?: error("Create Arc Section Dialog is not open")
fun getCreateArcSectionDialog(): CreateArcSectionDialogView? = robot.getOpenDialog()

fun CreateArcSectionDialogView.createSectionForTemplate(templateName: String)
{
    val templateItem = driver().sectionTypeSelection.selection.findItem { it == templateName }!!
    drive {
        templateItem.fire()
        primaryButton.fire()
    }
}