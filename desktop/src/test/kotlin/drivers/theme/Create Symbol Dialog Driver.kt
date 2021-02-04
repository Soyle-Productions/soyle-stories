package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.scene.sceneSymbols.drive
import com.soyle.stories.desktop.view.theme.createSymbolDialog.CreateSymbolDialogDriver
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.entities.Theme
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.themeList.ThemeList
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun ThemeList.openCreateSymbolDialogForThemeNamed(themeName: String): CreateSymbolDialog {
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeName)
    val createSymbolItem = themeItemContextMenu.items.find { it.text == "Create Symbol" }!!
    robot.interact {
        tree.selectionModel.select(themeItem)
        createSymbolItem.fire()
    }
    return getCreateSymbolDialog() ?: error("Theme list did not properly open Create Symbol Dialog for theme \"$themeName\"")
}

fun getCreateSymbolDialogOrError(): CreateSymbolDialog =
    getCreateSymbolDialog() ?: error("Create Symbol Dialog is not open in this project")

fun getCreateSymbolDialog(): CreateSymbolDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateSymbolDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun SymbolsInSceneView.givenCreatingNewSymbolForTheme(theme: Theme): CreateSymbolDialog =
    getCreateSymbolDialog() ?: openCreateSymbolDialogForTheme(theme).run { getCreateSymbolDialogOrError() }

fun SymbolsInSceneView.givenCreatingNewThemeAndSymbol(): CreateSymbolDialog =
    getCreateSymbolDialog() ?: openCreateSymbolDialog().run { getCreateSymbolDialogOrError() }

fun SymbolsInSceneView.openCreateSymbolDialogForTheme(theme: Theme)
{
    drive {
        val pinSymbolButton = pinSymbolButton!!
        if (! pinSymbolButton.isShowing) pinSymbolButton.fire()
        val themeItem = pinSymbolButton.themeItem(theme.id)!!
        themeItem.createSymbolOption.fire()
    }
}

fun SymbolsInSceneView.openCreateSymbolDialog()
{
    drive {
        val pinSymbolButton = pinSymbolButton!!
        if (! pinSymbolButton.isShowing) pinSymbolButton.fire()
        pinSymbolButton.createThemeAndSymbolOption.fire()
    }
}

fun CreateSymbolDialog.createSymbolWithName(symbolName: String) {
    val driver = CreateSymbolDialogDriver(this)
    val nameInput = driver.nameInput
    driver.interact {
        nameInput.text = symbolName
        nameInput.fireEvent(ActionEvent())
    }
}

fun CreateSymbolDialog.createSymbolAndThemeNamed(themeName: String, symbolName: String) {
    val driver = CreateSymbolDialogDriver(this)
    val nameInput = driver.nameInput
    if (!driver.themeWillBeCreated) {
        driver.interact { driver.themeToggleButton.fire() }
    }
    val themeNameInput = driver.themeNameInput
    driver.interact {
        themeNameInput.text = themeName
        nameInput.text = symbolName
        nameInput.fireEvent(ActionEvent())
    }
}