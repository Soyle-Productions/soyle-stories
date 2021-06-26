package com.soyle.stories.desktop.view.common.components.dataDisplay

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.exists
import com.soyle.stories.desktop.view.common.NodeAccess
import javafx.scene.Node
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot

class `Chip Access`(val chip: Chip) : NodeAccess<Chip>(chip) {
    companion object {
        fun Chip.access() = `Chip Access`(this)
    }

    val deleteButton: Node? by temporaryChild(Chip.Styles.chipDeleteIcon)

}