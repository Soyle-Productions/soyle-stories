package com.soyle.stories.desktop.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.*
import com.soyle.stories.desktop.view.project.close.CloseProjectPromptView
import com.soyle.stories.desktop.view.project.start.StartProjectPromptView
import com.soyle.stories.desktop.view.project.workspace.Workspace
import com.soyle.stories.desktop.view.project.none.WelcomeWindow
import kotlinx.coroutines.runBlocking

fun SoyleStories() = runBlocking {

    awaitApplication {

        val soyleStories = SoyleStoriesState.remember()

        when (soyleStories) {
            is SoyleStoriesState.Uninitialized -> {
                LaunchedEffect(soyleStories) { soyleStories.load().join() }
                Window(visible = false, create = ::ComposeWindow, dispose = ComposeWindow::dispose) {}
            }
            is SoyleStoriesState.Loading -> LoadingPreviousStateIndicatorWindow(onCancel = soyleStories::cancelLoad)
            is SoyleStoriesState.Loaded -> {
                val openProjects = remember { soyleStories.openProjects }

                MaterialTheme(
                    colors = MaterialTheme.colors.copy(
                        primary = Color(0x60, 0x40, 0x8B),
                        secondary = Color(0x3F, 0x7B, 0x88))
                ) {
                    CloseProjectPromptView(onExit = ::exitApplication)

                    WelcomeWindow(::exitApplication, isOpen = openProjects.isEmpty())
                    openProjects.forEach {
                        Workspace(it)
                    }
                    StartProjectPromptView()
                }
            }
        }
    }
}

@Composable
fun LoadingPreviousStateIndicatorWindow(
    onCancel: () -> Unit,
) {
    Window(
        onCloseRequest = onCancel,
        title = "Loading Previous State",
        state = rememberWindowState(
            WindowPlacement.Floating,
            false,
            WindowPosition(Alignment.Center)
        )
    ) {
        MaterialTheme {
            CancellableProgressIndicator(onCancel = onCancel)
        }
    }
}