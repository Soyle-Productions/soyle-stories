package com.soyle.stories.desktop.view

import javafx.scene.input.KeyCode
import org.testfx.api.FxRobot

fun FxRobot.type(text: String) {
    interact {
        text.forEach {
            if (it.isUpperCase()) {
                press(KeyCode.SHIFT)
                type(KeyCode.getKeyCode(it.toString()))
                release(KeyCode.SHIFT)
            } else {
                val keyCode = KeyCode.getKeyCode(it.toUpperCase().toString()) ?: uncommonKeyCode(it)
                if (keyCode == null) println("$it has a null keyCode")
                else type(keyCode)
            }
        }
    }
}

private fun uncommonKeyCode(character: Char): KeyCode?
{
    return when (character)
    {
        ' ' -> KeyCode.SPACE
        '\'' -> KeyCode.QUOTE
        '\n' -> KeyCode.ENTER
        else -> null
    }
}