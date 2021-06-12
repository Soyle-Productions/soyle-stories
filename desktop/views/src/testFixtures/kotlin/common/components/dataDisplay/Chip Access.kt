package com.soyle.stories.desktop.view.common.components.dataDisplay

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.exists
import javafx.scene.Node
import org.testfx.api.FxRobot

class `Chip Access`(val chip: Chip) : FxRobot() {
    companion object {
        fun Chip.access() = `Chip Access`(this)
    }

    val deleteButton: Node?
        get() = from(chip).lookup(".${Chip.Styles.chipDeleteIcon.name}").queryAll<Node>().firstOrNull()?.takeIf {
            it.exists
        }
}