package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.deleteValueWebDialog.DeleteValueWebDialogDriver
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebDriver
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialog
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import tornadofx.uiComponent

fun ValueOppositionWebs.openDeleteValueWebDialogForValueWebNamed(valueWebName: String): DeleteValueWebDialog?
{
    val driver = ValueOppositionWebDriver(this)
    val valueWebItem = driver.getValueWebItemWithNameOrError(valueWebName)
    driver.interact { valueWebItem.fire() }
    val deleteOption = driver.actions.delete()
    driver.interact { deleteOption.fire() }
    return getDeleteValueWebDialog()
}

fun getDeleteValueWebDialog(): DeleteValueWebDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteValueWebDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun DeleteValueWebDialog.confirmDeleteValueWeb()
{
    val driver = DeleteValueWebDialogDriver(this)
    val deleteButton = driver.getDeleteButton()
    driver.interact { deleteButton.fire() }
}