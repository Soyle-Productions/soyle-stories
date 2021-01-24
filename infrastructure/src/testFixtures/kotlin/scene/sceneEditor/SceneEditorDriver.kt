package com.soyle.stories.desktop.view.scene.sceneEditor

import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import javafx.scene.layout.Region
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class SceneEditorDriver(private val sceneEditor: SceneEditorView) : FxRobot() {

    companion object
    {
        fun SceneEditorView.drive(driving: SceneEditorDriver.() -> Unit = {}) = driver().apply { interact { driving() } }
        fun SceneEditorView.driver() = SceneEditorDriver(this)
    }

    fun getProseEditor(): ProseEditorView
    {
        return from(sceneEditor.root).lookup(".prose-editor").query<Region>().uiComponent()!!
    }

}