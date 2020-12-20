package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.drive
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.sceneEditor.SceneEditorScope
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import com.soyle.stories.scene.sceneList.SceneList
import javafx.scene.input.KeyCode
import tornadofx.FX

fun SceneList.givenSceneEditorToolHasBeenOpened(scene: Scene): SceneEditorView =
    scope.getSceneEditorTool(scene) ?: openSceneEditorTool(scene).let { scope.getSceneEditorToolOrError(scene) }

fun ProjectScope.getSceneEditorToolOrError(scene: Scene): SceneEditorView =
    getSceneEditorTool(scene) ?: error("No Scene Editor open for scene ${scene.name}")

fun ProjectScope.getSceneEditorTool(scene: Scene): SceneEditorView?
{
    return toolScopes.asSequence()
        .filterIsInstance<SceneEditorScope>()
        .find { it.type.sceneId == scene.id }
        ?.let { FX.getComponents(it)[SceneEditorView::class] as? SceneEditorView }
}

fun SceneList.openSceneEditorTool(scene: Scene)
{
    val driver = SceneListDriver(this)
    val item = driver.getSceneItemOrError(scene.name.value)
    driver.interact {
        driver.getTree().selectionModel.select(item)
        with(driver) {
            item.getSceneEditorItem().fire()
        }
    }
}

fun SceneEditorView.requestStoryElementsMatching(query: String)
{
    val keyCodes = query.asSequence()
        .map(Character::toString)
        .map(String::toUpperCase)
        .map(KeyCode::getKeyCode)
        .toList()
        .toTypedArray()
    driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            press(KeyCode.SHIFT).type(KeyCode.DIGIT2).release(KeyCode.SHIFT)
            type(*keyCodes)
        }
}

