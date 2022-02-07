package com.soyle.stories.desktop.view.project.none

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

@Composable
fun WelcomeWindow(
    onCloseRequest: () -> Unit,
    isOpen: Boolean
) {
    if (isOpen) {
        Window(
            onCloseRequest = onCloseRequest,
            title = "Welcome",
            state = rememberWindowState(
                WindowPlacement.Floating,
                false,
                WindowPosition(Alignment.Center)
            )
        ) {
            MaterialTheme {
                WelcomeView()
            }
        }
    }
}