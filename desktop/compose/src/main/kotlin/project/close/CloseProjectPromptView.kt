package com.soyle.stories.desktop.view.project.close

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.soyle.stories.desktop.view.di.koin

@Composable
fun CloseProjectPromptView(
    onExit: () -> Unit,
    viewModel: CloseProjectPromptViewModel = koin.get()
) {
    remember(
        viewModel.openState()
    ) { viewModel }

    if (viewModel.isOpen) {
        Window(
            onCloseRequest = viewModel::cancel,
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center)
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Do you want to close the project or exit Soyle Stories")
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = viewModel::closeProject) {
                        Text("Close Project")
                    }
                    Button(onClick = onExit) {
                        Text("Exit Soyle Stories")
                    }
                }
            }
        }
    }

}