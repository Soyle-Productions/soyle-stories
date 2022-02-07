package com.soyle.stories.desktop.view.project.workspace.tool

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

class ToolViewModel(
    open: Boolean,
    val name: String
) {

    private val open = mutableStateOf(open)
    fun isOpen(): State<Boolean> = open
    val isOpen: Boolean by isOpen()

    fun close() { open.value = false }

}