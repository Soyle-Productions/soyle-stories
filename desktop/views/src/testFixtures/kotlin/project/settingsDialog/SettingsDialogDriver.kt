package com.soyle.stories.desktop.view.project.settingsDialog

import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.settingsDialog.SettingsDialog
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import org.testfx.api.FxRobot

class SettingsDialogDriver(private val dialog: SettingsDialog) : FxRobot() {

    fun getDialogCheckbox(dialogType: DialogType): CheckBox
    {
        return from(dialog.root).lookup("#${dialogType::class.simpleName}").query()
    }

    fun getSaveButton(): Button
    {
        return from(dialog.root).lookup("#save").query()
    }

}