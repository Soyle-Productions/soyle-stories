package com.soyle.stories.desktop.view.scene.sceneEditor

import com.soyle.stories.prose.proseEditor.ProseEditorAssertions
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import org.junit.jupiter.api.Assertions.assertEquals

class SceneEditorAssertions private constructor(private val driver: SceneEditorDriver) {
    companion object {
        fun assertThat(sceneEditor: SceneEditorView, assertions: SceneEditorAssertions.() -> Unit)
        {
            SceneEditorAssertions(SceneEditorDriver(sceneEditor)).assertions()
        }
    }

    fun hasConflict(expectedConflict: String) {
        assertEquals(expectedConflict, driver.conflictFieldInput.text)
    }

    fun hasResolution(expectedResolution: String) {
        assertEquals(expectedResolution, driver.resolutionFieldInput.text)
    }

    fun andProseEditor(proseEditorAssertions: ProseEditorAssertions.() -> Unit) {
        ProseEditorAssertions.assertThat(driver.getProseEditor(), proseEditorAssertions)
    }
}