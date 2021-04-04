package com.soyle.stories.desktop.view.character.createArcSectionDialog

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.common.components.text.TextStyles
import javafx.scene.control.Button
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.layout.VBox
import org.testfx.api.FxRobot

class CreateArcSectionDialogDriver(private val dialog: CreateArcSectionDialogView) : FxRobot() {

    val sectionTypeSelection: SectionTypeSelectionFieldDriver
        get() = SectionTypeSelectionFieldDriver(from(dialog.root).lookup(".${TextStyles.section.name}").query())

    val primaryButton: Button
        get() = from(dialog.root).lookup(".button-bar .button").queryButton()

    inner class SectionTypeSelectionFieldDriver(private val section: VBox)
    {
        val label: String
            get() = from(section).lookup(".${TextStyles.fieldLabel}").queryLabeled().text

        val selection: SectionTypeSelectionFieldDriver
            get() = SectionTypeSelectionFieldDriver(from(section).lookup(".menu-button").query())

        inner class SectionTypeSelectionFieldDriver(private val menuButton: MenuButton)
        {
            fun findItem(predicate: (String) -> Boolean) = menuButton.items.find {
                when (it) {
                    is CustomMenuItem -> predicate((it.content as Label).text)
                    else -> predicate(it.text)
                }
            }
        }
    }

}


fun CreateArcSectionDialogView.driver() = CreateArcSectionDialogDriver(this)
inline fun CreateArcSectionDialogView.drive(crossinline interaction: CreateArcSectionDialogDriver.() -> Unit) {
    with (driver()) {
        interact { interaction() }
    }
}