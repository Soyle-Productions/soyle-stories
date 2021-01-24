package com.soyle.stories.desktop.view.scene.sceneEditor

import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorAssertions
import com.soyle.stories.scene.sceneEditor.SceneEditorView

class SceneEditorAssertions private constructor(private val driver: SceneEditorDriver) {
    companion object {
        fun assertThat(sceneEditor: SceneEditorView, assertions: SceneEditorAssertions.() -> Unit)
        {
            SceneEditorAssertions(SceneEditorDriver(sceneEditor)).assertions()
        }
    }

    fun andProseEditor(proseEditorAssertions: ProseEditorAssertions.() -> Unit) {
        ProseEditorAssertions.assertThat(driver.getProseEditor(), proseEditorAssertions)
    }
}