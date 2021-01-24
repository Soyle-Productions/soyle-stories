package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.startProjectDialog.StartProjectDialog
import com.soyle.stories.soylestories.SoyleStories
import com.soyle.stories.soylestories.welcomeScreen.WelcomeScreen
import javafx.scene.control.Button
import tornadofx.FX
import tornadofx.uiComponent

fun getWelcomeScreen(): WelcomeScreen? {
    return robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<WelcomeScreen>() }
        .firstOrNull { it.currentStage?.isShowing == true }
}

fun getWelcomeScreenOrError(): WelcomeScreen =
    getWelcomeScreen() ?: throw NoSuchElementException("Welcome Screen not open in application")

fun WelcomeScreen.getCreateNewProjectButton(): Button
{
    return robot.from(this.root).lookup("#createNewProject").query()
}