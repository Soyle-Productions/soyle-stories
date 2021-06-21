package com.soyle.stories.common

import javafx.application.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext

suspend fun guiUpdate(block: () -> Unit) {
    if (! Platform.isFxApplicationThread()) {
        withContext(Dispatchers.JavaFx) { block() }
    } else block()
}