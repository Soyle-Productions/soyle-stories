package com.soyle.stories.desktop.view.scene.sceneEditor

import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import javafx.scene.layout.Region
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class SceneEditorDriver(private val sceneEditor: SceneEditorView) : FxRobot() {

    fun getProseEditor(): ProseEditorView
    {
        return from(sceneEditor.root).lookup(".prose-editor").query<Region>().uiComponent()!!
    }

}