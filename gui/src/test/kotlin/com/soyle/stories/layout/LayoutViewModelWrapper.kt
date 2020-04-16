package com.soyle.stories.layout

import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.LayoutViewModel
import com.soyle.stories.project.layout.StaticToolViewModel

class LayoutViewModelWrapper(
  private val makeTool: (StaticToolViewModel) -> Unit,
  private val makeDialog: (Dialog) -> Unit
) : LayoutView {

	private var viewModel = LayoutViewModel()

	val staticTools: List<StaticToolViewModel>
		get() = viewModel.staticTools
	val openDialogs
		get() = viewModel.openDialogs

	val isCreateLocationDialogVisible: Boolean
		get() = viewModel.openDialogs.containsKey(Dialog.CreateLocation::class)
	val isDeleteLocationDialogVisible: Boolean
		get() = viewModel.openDialogs.containsKey(Dialog.DeleteLocation::class)

	override fun update(update: LayoutViewModel.() -> LayoutViewModel) {
		synchronized(this) {
			viewModel = viewModel.update()
			staticTools.forEach {
				makeTool(it)
			}
			viewModel.openDialogs.forEach {
				makeDialog(it.value)
			}
		}
	}
}