package com.soyle.stories.desktop.view.theme.createSymbolDialog

import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot
import tornadofx.Field

class CreateSymbolDialogDriver(private val dialog: CreateSymbolDialog) : FxRobot() {

    val nameInput: TextInputControl
        get() = from(dialog.root).lookup("#name-input").query<TextField>()

    private val themeLink: Node
        get() = from(dialog.root).lookup(".theme-link").query()

    private val selectThemeField: Field
        get() = from(themeLink).lookup(".field").queryAll<Field>().find { it.text == "Select Existing Theme" }!!

    private val createThemeField: Field
        get() = from(themeLink).lookup(".field").queryAll<Field>().find { it.text == "Create New Theme" }!!

    val themeNameInput: TextInputControl
        get() = from(createThemeField).lookup(".text-field").queryTextInputControl()

    val themeToggleButton: Button
        get() = from(themeLink).lookup(".button").queryButton()

    val createNewThemeIsAvailable: Boolean
        get() = themeLink.visibleProperty().get()

    val themeWillBeCreated: Boolean
        get() = createNewThemeIsAvailable && createThemeField.visibleProperty().get()

    val existingThemeWillBeSelected: Boolean
        get() = createNewThemeIsAvailable && selectThemeField.visibleProperty().get()
}