package com.soyle.stories.soylestories.confirmExitDialog

class ConfirmExitDialogController(
  private val confirmExitDialogPresenter: ConfirmExitDialogPresenter
) : ConfirmExitDialogViewListener {
	override fun initializeConfirmExitDialog() {
		confirmExitDialogPresenter.displayConfirmExitDialog()
	}
}