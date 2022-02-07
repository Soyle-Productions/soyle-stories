package com.soyle.stories.desktop.view.project.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import org.koin.core.context.GlobalContext.get

@Composable
fun StartProjectPromptView(viewModel: StartProjectPromptViewModel = get().get()) {
    remember(
        viewModel.isOpen(),
        viewModel.name(),
        viewModel.nameError(),
        viewModel.directory(),
        viewModel.directoryError(),
    ) { viewModel }

    if (viewModel.isOpen) {
        Window(
            onCloseRequest = viewModel::close
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        label = { Text("Directory") },
                        value = viewModel.directory,
                        onValueChange = { viewModel.directory = it },
                        isError = viewModel.directoryError != null,
                        trailingIcon = { Text(viewModel.directoryError ?: "") }
                    )
                    Button(
                        onClick = {}
                    ){ Text("Choose Directory") }
                }
                TextField(
                    label = { Text("Name") },
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    isError = viewModel.nameError != null,
                    trailingIcon = { Text(viewModel.nameError ?: "") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = viewModel::close,
                        border = BorderStroke(2.dp, MaterialTheme.colors.secondary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.secondary)
                    ) { Text("Cancel") }
                    Button(
                        onClick = viewModel::complete,
                        enabled = viewModel.directoryError != null && viewModel.nameError != null
                    ) { Text("Start") }
                }
            }
        }
    }
}