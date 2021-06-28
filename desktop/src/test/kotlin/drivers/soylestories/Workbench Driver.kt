package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.desktop.config.drivers.project.ProjectDriver
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.soylestories.SoyleStories
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import tornadofx.FX

fun ProjectScope.getWorkbenchOrError(): WorkBench =
    getWorkbench() ?: throw NoSuchElementException("No Workbench open in this project scope")

fun ProjectScope.getWorkbench(): WorkBench? =
    FX.getComponents(this)[WorkBench::class]?.let { it as? WorkBench }?.takeIf { it.currentStage?.isShowing == true }

fun SoyleStories.getAnyOpenWorkbenchOrError(): WorkBench =
    getAnyOpenWorkbench() ?: run {
        val project = ProjectDriver(soyleStories).getOpenProjectOrError()
        soyleStories.getWorkbenchForProjectOrError(project.id.uuid)
    }

fun SoyleStories.getAnyOpenWorkbench(): WorkBench? {
    val project = ProjectDriver(soyleStories).getOpenProject() ?: return null
    return soyleStories.getWorkbenchForProject(project.id.uuid)
}

fun WorkBench.getMenuBar(): MenuBar
{
    return robot.from(this.root)
        .lookup(".menu-bar")
        .query()
}

fun MenuItem.collectItems(): Sequence<MenuItem> = when (this) {
    is Menu -> sequenceOf(this) + items.asSequence().flatMap { it.collectItems() }
    else -> sequenceOf(this)
}

fun WorkBench.findMenuItemById(id: String): MenuItem? = getMenuBar()
    .menus.asSequence()
    .flatMap { it.collectItems() }
    .find { it.id == id }