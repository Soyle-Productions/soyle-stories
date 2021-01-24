package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebDriver
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import io.cucumber.messages.internal.com.google.protobuf.Value
import javafx.event.ActionEvent
import tornadofx.FX

fun WorkBench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName: String): ValueOppositionWebs =
    getValueOppositionWebToolForThemeNamed(themeName) ?: givenThemeListToolHasBeenOpened()
        .openValueWebToolForThemeNamed(themeName)

fun ThemeList.openValueWebToolForThemeNamed(themeName: String): ValueOppositionWebs
{
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeName)
    val compareValuesItem = themeItemContextMenu.items.find { it.text == "Compare Values" }!!
    robot.interact {
        tree.selectionModel.select(themeItem)
        compareValuesItem.fire()
    }
    return scope.get<WorkBench>().getValueOppositionWebToolForThemeNamed(themeName)
        ?: error("Theme list did not properly open Value Web Tool for theme \"$themeName\"")
}

fun WorkBench.getValueOppositionWebToolForThemeNamed(themeName: String): ValueOppositionWebs?
{
    val theme = ThemeDriver(this).getThemeByNameOrError(themeName)
    return scope.toolScopes
        .filterIsInstance<ValueOppositionWebsScope>()
        .find { it.themeId == theme.id.uuid }
        ?.let { FX.getComponents(it)[ValueOppositionWebs::class] as? ValueOppositionWebs }
}

fun ValueOppositionWebs.renameValueWebTo(valueWebName: String, newName: String)
{
    givenValueWebHasBeenSelectedNamed(valueWebName)
    val driver = ValueOppositionWebDriver(this)
    val valueWebInput = driver.getValueWebNameInput()
    driver.interact {
        valueWebInput.show()
        valueWebInput.editedText = newName
        valueWebInput.commit()
    }
}

fun ValueOppositionWebs.givenValueWebHasBeenSelectedNamed(valueWebName: String)
{
    val driver = ValueOppositionWebDriver(this)
    val valueWebItem = driver.getValueWebItemWithNameOrError(valueWebName)
    driver.interact { valueWebItem.fire() }
}

fun ValueOppositionWebs.createNewOppositionValue()
{
    val driver = ValueOppositionWebDriver(this)
    val createOppositionValueButton = driver.getCreateOppositionValueButton()
    driver.interact { createOppositionValueButton.fire() }
}

fun ValueOppositionWebs.renameOppositionValueTo(oppositionValueIndex: Int, newName: String) {
    val driver = ValueOppositionWebDriver(this)
    val oppositionValueNameInput = driver.getOppositionValueNameInput(oppositionValueIndex)
    driver.interact {
        oppositionValueNameInput.show()
        oppositionValueNameInput.editedText = newName
        oppositionValueNameInput.commit()
    }
}

fun ValueOppositionWebs.deleteOppositionValue(oppositionValueIndex: Int) {
    val driver = ValueOppositionWebDriver(this)
    val oppositionValueRemoveButton = driver.getOppositionValueRemoveButton(oppositionValueIndex)
    driver.interact { oppositionValueRemoveButton.fire() }
}