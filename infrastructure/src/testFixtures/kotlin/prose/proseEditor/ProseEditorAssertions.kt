package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.prose.proseEditor.ProseEditorView
import org.junit.jupiter.api.Assertions.assertEquals

class ProseEditorAssertions private constructor(private val driver: ProseEditorDriver) {
    companion object {
        fun assertThat(proseEditor: ProseEditorView, assertions: ProseEditorAssertions.() -> Unit) {
            ProseEditorAssertions(ProseEditorDriver(proseEditor)).assertions()
        }
    }

    fun hasContent(expectedContent: String)
    {
        assertEquals(expectedContent, driver.getContent())
    }
}