package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WindowChildView(
    viewModel: WindowChildViewModel,
    modifier: Modifier = Modifier,
) {
    when (viewModel) {
        is WindowChildSplitterViewModel -> WindowChildSplitterView(viewModel, modifier)
        is ToolStackViewModel -> ToolStackView(viewModel, modifier)
    }
}