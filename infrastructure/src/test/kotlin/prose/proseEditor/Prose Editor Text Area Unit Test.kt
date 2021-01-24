package com.soyle.stories.prose.proseEditor

import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit

class `Prose Editor Text Area Unit Test` : FxRobot() {

    private val textArea = ProseEditorTextArea()

    init {
        FxToolkit.registerPrimaryStage().apply {
            interact {
                scene = Scene(VBox(textArea))
                width = 300.0
                height = 300.0
                show()
            }
        }
    }

    @Test
    fun `contiguous basic text should collapse`() {
        interact {
            repeat(5) {
                textArea.append(BasicText("text $it "), listOf())
            }
        }
        assertEquals(1, textArea.paragraphs.size)
        assertEquals("text 0 text 1 text 2 text 3 text 4 ", textArea.text)
        assertEquals(1, textArea.paragraphs.flatMap { it.segments }.size)
    }

    @Test
    fun `basic text with newline should be split into paragraphs`() {
        interact {
            repeat(5) {
                textArea.appendText("text\n $it ")
            }
        }
        assertEquals("text\n 0 text\n 1 text\n 2 text\n 3 text\n 4 ", textArea.text)
        assertEquals(6, textArea.paragraphs.size)
        assertEquals(listOf(
            BasicText("text\n"),
            BasicText(" 0 text\n"),
            BasicText(" 1 text\n"),
            BasicText(" 2 text\n"),
            BasicText(" 3 text\n"),
            BasicText(" 4 ")
        ), textArea.paragraphs.flatMap { it.segments })
    }

    @Test
    fun `typed newline characters create new paragraphs`() {
        interact {
            textArea.requestFocus()
            type(KeyCode.A, KeyCode.B, KeyCode.ENTER, KeyCode.C, KeyCode.D, KeyCode.ENTER, KeyCode.E)
        }
        assertEquals(3, textArea.paragraphs.size)
        assertEquals("ab\ncd\ne", textArea.text)
        assertEquals(listOf(
            BasicText("ab\n"),
            BasicText("cd\n"),
            BasicText("e")
        ), textArea.paragraphs.flatMap { it.segments })
    }

}