package com.soyle.stories.desktop.view

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun CancellableProgressIndicator(
    cancelText: String = "Cancel",
    onCancel: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator()
        Button(onClick = onCancel) { Text(cancelText) }
    }
}