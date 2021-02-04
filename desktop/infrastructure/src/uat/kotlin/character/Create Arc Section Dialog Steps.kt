package com.soyle.stories.character

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.project.ProjectScope
import javafx.stage.Stage
import org.testfx.api.FxRobot
import tornadofx.uiComponent

private val robot = FxRobot()

fun ProjectScope.getCreateArcSectionDialog(): CreateArcSectionDialogView? {
    return robot.listWindows()
        .asSequence()
        .filterIsInstance<Stage>()
        .mapNotNull { it.scene.root.uiComponent<CreateArcSectionDialogView>()?.takeIf { it.currentStage?.isShowing == true } }
        .firstOrNull()
}
fun ProjectScope.getCreateArcSectionDialogOrError(): CreateArcSectionDialogView =
    getCreateArcSectionDialog() ?: throw NoSuchElementException("Create Arc Section Dialog View not opened.")