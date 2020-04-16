package com.soyle.stories.location.createLocationDialog

class CreateLocationDialogViewModelWrapper : CreateLocationDialogView {

	private var viewModel: CreateLocationDialogViewModel? = null

	val errorMessage: String?
		get() = viewModel!!.errorMessage

	override fun update(update: CreateLocationDialogViewModel?.() -> CreateLocationDialogViewModel) {
		viewModel = viewModel.update()
	}

	override fun updateOrInvalidated(update: CreateLocationDialogViewModel.() -> CreateLocationDialogViewModel) {
		viewModel = viewModel?.update()
	}

}