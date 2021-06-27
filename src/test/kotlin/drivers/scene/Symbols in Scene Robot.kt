package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.sceneSymbols.drive
import com.soyle.stories.desktop.view.scene.sceneSymbols.driver
import com.soyle.stories.di.get
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import javafx.scene.control.Button
import tornadofx.FX

fun WorkBench.givenSymbolsInSceneToolHasBeenOpened(): SymbolsInSceneView =
    getOpenSymbolsInSceneTool() ?: openSymbolsInSceneTool().run { getOpenSymbolsInSceneToolOrError() }

fun WorkBench.getOpenSymbolsInSceneToolOrError(): SymbolsInSceneView =
    getOpenSymbolsInSceneTool() ?: error("No Symbols in Scene tool is open in the project")

fun WorkBench.getOpenSymbolsInSceneTool(): SymbolsInSceneView?
{
    return (FX.getComponents(scope)[SymbolsInSceneView::class] as? SymbolsInSceneView)?.takeIf { it.currentStage?.isShowing == true }
}



fun WorkBench.openSymbolsInSceneTool()
{
    findMenuItemById("tools_symbols in scene")!!
        .apply { robot.interact { fire() } }
}

fun SymbolsInSceneView.givenFocusedOn(scene: Scene): SymbolsInSceneView
{
    if (! driver().isFocusedOn(scene)) focusOn(scene)
    return this
}

fun SymbolsInSceneView.focusOn(scene: Scene)
{
    scope.get<WorkBench>().givenSceneListToolHasBeenOpened()
        .selectScene(scene)
}

fun SymbolsInSceneView.pinSymbol(theme: Theme, symbolName: String)
{
    drive {
        val symbolChip = getSymbolChip(symbolName)
        if (symbolChip != null) (symbolChip.graphic as Button).fire()
        else {
            if (! pinSymbolButton!!.isShowing) pinSymbolButton!!.fire()
            val themeItem = pinSymbolButton!!.themeItem(theme.id)!!
            val symbolItem = themeItem.symbolItem(symbolName)!!
            symbolItem.fire()
        }
    }
}

fun SymbolsInSceneView.unpinSymbol(theme: Theme, symbolName: String)
{
    drive {
        val symbolChip = getSymbolChip(symbolName)!!
        if (symbolChip.isPinned()) {
            (symbolChip.graphic as Button).fire()
        }
    }
}