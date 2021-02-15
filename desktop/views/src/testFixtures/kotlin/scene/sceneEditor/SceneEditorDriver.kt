package com.soyle.stories.desktop.view.scene.sceneEditor

import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import javafx.scene.layout.Region
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class SceneEditorDriver(private val sceneEditor: SceneEditorView) : FxRobot() {

    companion object
    {
        fun SceneEditorView.drive(driving: SceneEditorDriver.() -> Unit = {}) = driver().apply { interact { driving() } }
        fun SceneEditorView.driver() = SceneEditorDriver(this)
    }

    private val conflictField: Node
        get() = from(sceneEditor.root).lookup("#conflict-field").query<Node>()

    val conflictFieldLabel: Labeled
        get() = from(conflictField).lookup(".label").query<Label>()

    val conflictFieldInput: TextInputControl
        get() = from(conflictField).lookup(".text-field").queryTextInputControl()

    private val resolutionField: Node
        get() = from(sceneEditor.root).lookup("#resolution-field").query<Node>()

    val resolutionFieldInput: TextInputControl
        get() = from(resolutionField).lookup(".text-field").queryTextInputControl()

    fun getProseEditor(): ProseEditorView
    {
        return from(sceneEditor.root).lookup(".prose-editor").query<Region>().uiComponent()!!
    }

}