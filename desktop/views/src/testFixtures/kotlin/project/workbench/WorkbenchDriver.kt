package com.soyle.stories.desktop.view.project.workbench

import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.create.CreateScenePromptPresenter
import com.soyle.stories.scene.create.CreateScenePromptView
import com.soyle.stories.scene.delete.DeleteScenePromptView
import org.testfx.api.FxRobot
import tornadofx.UIComponent
import tornadofx.uiComponent

class WorkbenchDriver(private val workbench: WorkBench) : FxRobot() {

    fun getCreateSceneDialog(): CreateScenePromptView? = getOpenDialog()

    fun getConfirmDeleteSceneDialog(): DeleteScenePromptView? = getOpenDialog()
}

inline fun <reified T : UIComponent> FxRobot.getOpenDialog(noinline filter: (T) -> Boolean = {true}): T? =
    listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<T>() }
        .filter(filter)
        .firstOrNull { it.currentStage?.isShowing == true }