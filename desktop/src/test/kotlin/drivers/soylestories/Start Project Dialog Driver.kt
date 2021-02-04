package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.project.startProjectDialog.StartProjectDialog
import com.soyle.stories.soylestories.welcomeScreen.WelcomeScreen
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import tornadofx.lookup
import tornadofx.uiComponent


fun WelcomeScreen.givenStartProjectDialogHasBeenOpened(): StartProjectDialog =
    getStartProjectDialog() ?: openStartProjectDialog()

fun WelcomeScreen.openStartProjectDialog(): StartProjectDialog
{
    val createNewProjectButton = getCreateNewProjectButton()
    robot.interact { robot.clickOn(createNewProjectButton) }
    return getStartProjectDialogOrError()
}

fun getStartProjectDialogOrError(): StartProjectDialog =
    getStartProjectDialog() ?: throw NoSuchElementException("No Start Project Dialog is open in the application")

fun getStartProjectDialog(): StartProjectDialog?
{
    return robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<StartProjectDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }
}

fun StartProjectDialog.getDirectoryInput(): TextField
{
    return robot.from(this.root)
        .lookup("#directory-input").query()
}

fun StartProjectDialog.getCreateButton(): Button
{
    return robot.from(this.root)
        .lookup("Create Project")
        .query()
}