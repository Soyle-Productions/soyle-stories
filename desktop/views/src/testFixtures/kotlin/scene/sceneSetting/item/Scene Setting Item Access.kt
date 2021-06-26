package com.soyle.stories.desktop.view.scene.sceneSetting.item

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.exists
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.desktop.view.common.components.dataDisplay.`Chip Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.item.`Scene Setting Item Access`.Companion.access
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import tornadofx.hasClass

class `Scene Setting Item Access`(val item: SceneSettingItemView) : NodeAccess<SceneSettingItemView>(item) {
    companion object {
        fun SceneSettingItemView.access() = `Scene Setting Item Access`(this)
        fun SceneSettingItemView.access(op: `Scene Setting Item Access`.() -> Unit) = `Scene Setting Item Access`(this).op()
        fun <T> SceneSettingItemView.drive(op: `Scene Setting Item Access`.() -> T): T = `Scene Setting Item Access`(this).run {
            var t: T? = null
            interact {
                t = op()
            }
            return t as T
        }
    }

    val deleteButton: MenuButton by mandatoryChild(SceneSettingItemView.Styles.options)

    /**
     * Returns [null] is the [deleteButton] is not showing
     */
    val removeOption: MenuItem?
        get() = deleteButton.items.find { it.id == "remove" }?.takeIf { deleteButton.isShowing }

    /**
     * Returns [null] is the [deleteButton] is not showing
     */
    val replaceOption: Menu?
        get() = deleteButton.items.find { it.id == "replace" }?.takeIf { deleteButton.isShowing } as? Menu

    val Menu.availableLocationItems: List<MenuItem>
        get() = items.filter { it.hasClass(SceneSettingItemView.Styles.availableLocation) }
}