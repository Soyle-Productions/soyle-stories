package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebDriver
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.di.get
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import io.cucumber.messages.internal.com.google.protobuf.Value
import javafx.event.ActionEvent
import org.junit.jupiter.api.Assertions.assertNotNull
import tornadofx.FX

fun WorkBench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName: String): ValueOppositionWebs =
    getValueOppositionWebToolForThemeNamed(themeName) ?: givenThemeListToolHasBeenOpened()
        .openValueWebToolForThemeNamed(themeName)

fun WorkBench.givenValueWebToolHasBeenOpenedFor(themeId: Theme.Id): ValueOppositionWebs =
    getOpenValueOppositionWebToolFor(themeId) ?: givenThemeListToolHasBeenOpened()
        .openValueWebToolFor(themeId)

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

fun ThemeList.openValueWebToolFor(themeId: Theme.Id): ValueOppositionWebs
{
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeId)
    val compareValuesItem = themeItemContextMenu.items.find { it.id == "compare-values" }!!
    robot.interact {
        tree.selectionModel.select(themeItem)
        compareValuesItem.fire()
    }
    return scope.get<WorkBench>().getOpenValueOppositionWebToolFor(themeId)
        ?: error("Theme list did not properly open Value Web Tool for $themeId")
}

fun WorkBench.getValueOppositionWebToolForThemeNamed(themeName: String): ValueOppositionWebs?
{
    val theme = ThemeDriver(this).getThemeByNameOrError(themeName)
    return getOpenValueOppositionWebToolFor(theme.id)
}

fun WorkBench.getOpenValueOppositionWebToolFor(themeId: Theme.Id): ValueOppositionWebs?
{
    return scope.toolScopes
        .filterIsInstance<ValueOppositionWebsScope>()
        .find { it.themeId == themeId.uuid }
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


fun ValueOppositionWebs.givenValueWebHasBeenCreatedNamed(valueWebName: String): ValueOppositionWebs
{
    val driver = ValueOppositionWebDriver(this)
    if (driver.getValueWebItemWithName(valueWebName) == null) {
        openCreateValueWebDialog()
            .createValueWebNamed(valueWebName)
    }
    assertNotNull(driver.getValueWebItemWithName(valueWebName))
    return this
}

fun ValueOppositionWebs.givenValueWebHasBeenSelectedNamed(valueWebName: String): ValueOppositionWebs
{
    val driver = ValueOppositionWebDriver(this)
    val valueWebItem = driver.getValueWebItemWithNameOrError(valueWebName)
    driver.interact { valueWebItem.fire() }
    return this
}

fun ValueOppositionWebs.createNewOppositionValue()
{
    val driver = ValueOppositionWebDriver(this)
    val createOppositionValueButton = driver.getCreateOppositionValueButton()
    driver.interact { createOppositionValueButton.fire() }
}

fun ValueOppositionWebs.renameOppositionValueTo(oppositionValueId: OppositionValue.Id, newName: String) {
    val driver = ValueOppositionWebDriver(this)
    val oppositionValueNameInput = driver.getOppositionValueNameInput(oppositionValueId)
    driver.interact {
        oppositionValueNameInput.show()
        oppositionValueNameInput.editedText = newName
        oppositionValueNameInput.commit()
    }
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