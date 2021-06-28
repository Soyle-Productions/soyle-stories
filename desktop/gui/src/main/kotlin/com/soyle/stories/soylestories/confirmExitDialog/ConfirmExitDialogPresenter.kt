package com.soyle.stories.soylestories.confirmExitDialog

class ConfirmExitDialogPresenter(
  private val view: ConfirmExitDialogView
) {
	fun displayConfirmExitDialog() {
		view.update {
			ConfirmExitDialogViewModel(
			  "Confirm Exit",
			  "Are you sure you want to exit Soyle Stories?",
			  "Exit",
			  "Cancel"
			)
		}
	}
}