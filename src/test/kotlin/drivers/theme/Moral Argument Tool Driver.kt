package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewDriver
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.moralArgument.MoralArgumentScope
import com.soyle.stories.theme.moralArgument.MoralArgumentSectionTypeViewModel
import com.soyle.stories.theme.moralArgument.MoralArgumentView
import com.soyle.stories.theme.themeList.ThemeList
import javafx.scene.input.MouseButton
import kotlinx.coroutines.withTimeout
import tornadofx.item

fun WorkBench.givenMoralArgumentToolHasBeenOpenedForTheme(theme: Theme): MoralArgumentView =
    getMoralArgumentToolForTheme(theme) ?: openMoralArgumentToolForTheme(theme)

fun WorkBench.getMoralArgumentToolForThemeOrError(theme: Theme): MoralArgumentView =
    getMoralArgumentToolForTheme(theme) ?: error("Moral Argument tool for ${theme.name} has not been opened")

fun WorkBench.getMoralArgumentToolForTheme(theme: Theme): MoralArgumentView?
{
    val toolScope = scope.toolScopes
        .filterIsInstance<MoralArgumentScope>()
        .find { it.themeId == theme.id.uuid.toString() }
    return toolScope?.get()
}

fun WorkBench.openMoralArgumentToolForTheme(theme: Theme): MoralArgumentView
{
    givenThemeListToolHasBeenOpened()
        .openMoralArgumentToolFor(theme.name)
    return getMoralArgumentToolForThemeOrError(theme)
}

fun ThemeList.openMoralArgumentToolFor(themeName: String)
{
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val treeItem = driver.getThemeItemOrError(themeName)
    val toolOption = themeItemContextMenu.items.find { it.text == "Outline the Moral Argument" }!!
    robot.interact {
        tree.selectionModel.select(treeItem)
        toolOption.fire()
    }
}

fun MoralArgumentView.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character: Character) =
    givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character.name)
fun MoralArgumentView.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName: String)
{
    val driver = MoralArgumentViewDriver(this)
    val perspectiveCharacterSelection = driver.getPerspectiveCharacterSelection()
    if (perspectiveCharacterSelection.text == characterName) return
    driver.interact {
        perspectiveCharacterSelection.show()
    }
    val characterItem = perspectiveCharacterSelection.items.find { it.text == characterName }
        ?: error("Could not find item with text $characterName in ${perspectiveCharacterSelection.items.map { it.text }}")
    driver.interact {
        characterItem.fire()
    }
}

fun MoralArgumentView.changeMoralProblemTo(moralProblem: String)
{
    val driver = MoralArgumentViewDriver(this)
    val moralProblemInput = driver.getMoralProblemFieldInput()
    driver.interact {
        moralProblemInput.requestFocus()
        moralProblemInput.text = moralProblem
        driver.getThemeLineFieldInput().requestFocus()
    }
}

fun MoralArgumentView.changeThemeLineTo(themeLine: String)
{
    val driver = MoralArgumentViewDriver(this)
    val themeLineInput = driver.getThemeLineFieldInput()
    driver.interact {
        themeLineInput.requestFocus()
        themeLineInput.text = themeLine
        driver.getMoralProblemFieldInput().requestFocus()
    }
}

fun MoralArgumentView.changeThematicRevelationTo(thematicRevelation: String)
{
    val driver = MoralArgumentViewDriver(this)
    val thematicRevelationInput = driver.getThematicRevelationFieldInput()
    driver.interact {
        thematicRevelationInput.requestFocus()
        thematicRevelationInput.text = thematicRevelation
        driver.getMoralProblemFieldInput().requestFocus()
    }
}

fun MoralArgumentView.givenMoralArgumentHasBeenPreparedToAddNewSection(index: Int? = null) =
    if (! preparedToAddNewSection(index)) prepareToAddNewSection(index) else Unit

fun MoralArgumentView.preparedToAddNewSection(index: Int? = null): Boolean
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelections = driver.getSectionTypeSelections()
    if (index == null) return sectionTypeSelections.any { it.isShowing }
    val showingIndex = sectionTypeSelections.indexOfFirst { it.isShowing }
    return showingIndex == index
}

fun MoralArgumentView.prepareToAddNewSection(index: Int? = null)
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelections =  driver.getSectionTypeSelections()
    val sectionTypeSelection = sectionTypeSelections.let {
        if (index == null) it.last()
        else it.toList()[index]
    }
    driver.interact {
        sectionTypeSelections.forEachIndexed { i, it -> if (it.isShowing && i != index) it.hide() }
    }
    driver.interact {
        sectionTypeSelection.show()
    }
}

fun MoralArgumentView.givenMoralArgumentHasBeenPreparedToMoveSection() {
    if (! isPreparedToMoveSection()) prepareToMoveSection()
}

fun MoralArgumentView.isPreparedToMoveSection(): Boolean
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelections = driver.getSectionTypeSelections()
    return sectionTypeSelections.any { it.isShowing }
}

fun MoralArgumentView.prepareToMoveSection()
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelections =  driver.getSectionTypeSelections()
    val sectionTypeSelection = sectionTypeSelections.first()
    driver.interact {
        sectionTypeSelection.show()
    }
}

fun MoralArgumentView.selectUnusedSectionType(): MoralArgumentSectionTypeViewModel
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelection = driver.getSectionTypeSelections().find { it.isShowing }!!
    val (viewModel, item) = sectionTypeSelection.items.asSequence()
        .map { (it.userData as MoralArgumentSectionTypeViewModel) to it }
        .find { it.first.canBeCreated }!!
    driver.interact {
        item.fire()
    }
    return viewModel
}

fun MoralArgumentView.selectUsedSectionType(): MoralArgumentSectionTypeViewModel
{
    val driver = MoralArgumentViewDriver(this)
    val sectionTypeSelection = driver.getSectionTypeSelections().find { it.isShowing }!!
    val (viewModel, item) = sectionTypeSelection.items.asSequence()
        .map { (it.userData as MoralArgumentSectionTypeViewModel) to it }
        .find { ! it.first.canBeCreated }!!
    driver.interact {
        item.fire()
    }
    return viewModel
}

fun MoralArgumentView.moveSectionToNewPosition(initialPosition: Int, newPosition: Int) {
    val driver = MoralArgumentViewDriver(this)
    val dragHandle = driver.getArcSectionDragHandle(initialPosition)
    driver.interact {
        driver.drag(dragHandle, MouseButton.PRIMARY)
            .dropTo(driver.getArcSectionLabel(newPosition))
    }
}

fun MoralArgumentView.removeFirstSectionWithName(name: String) {
    val driver = MoralArgumentViewDriver(this)
    val index = driver.getArcSectionLabels().indexOfFirst { it.text == name }
    if (index == -1) error("No arc section found with name $name")
    driver.interact {
        driver.getArcSectionRemoveButton(index)!!.fire()
    }
}