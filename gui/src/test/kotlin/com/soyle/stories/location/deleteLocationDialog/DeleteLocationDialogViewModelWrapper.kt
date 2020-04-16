package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.layout.LayoutViewModelWrapper
import com.soyle.stories.project.layout.Dialog

class DeleteLocationDialogViewModelWrapper(
  private val getLayoutModel: () -> LayoutViewModelWrapper
) : DeleteLocationDialogView {

	val dialog
		get() = getLayoutModel().openDialogs[Dialog.DeleteLocation::class] as? Dialog.DeleteLocation?

	private var viewModel: DeleteLocationDialogViewModel? = null

	val locationId: String?
		get() = viewModel!!.locationId
	val locationName: String?
		get() = viewModel!!.locationName

	override fun update(update: DeleteLocationDialogViewModel?.() -> DeleteLocationDialogViewModel) {
		viewModel = viewModel.update()
	}

	override fun updateOrInvalidated(update: DeleteLocationDialogViewModel.() -> DeleteLocationDialogViewModel) {
		viewModel = viewModel?.update()
	}
}