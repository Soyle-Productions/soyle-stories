package com.soyle.stories.soylestories.confirmExitDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModel
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel
import tornadofx.runLater

class ConfirmExitDialogModel : ConfirmExitDialogView, ItemViewModel<ConfirmExitDialogViewModel>() {

	val title = bind(ConfirmExitDialogViewModel::title)
	val header = bind(ConfirmExitDialogViewModel::header)
	val exitButton = bind(ConfirmExitDialogViewModel::exitButton)
	val cancelButton = bind(ConfirmExitDialogViewModel::cancelButton)
	val closingProject = find<ApplicationModel>().closingProject

	val isInvalid = SimpleBooleanProperty(true)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope as ApplicationScope)

	override val viewModel: ConfirmExitDialogViewModel? = item

	override fun update(update: ConfirmExitDialogViewModel?.() -> ConfirmExitDialogViewModel) {
		if (! Platform.isFxApplicationThread()) return runLater { update(update) }
		isInvalid.set(false)
		item = item.update()
	}

	override fun updateOrInvalidated(update: ConfirmExitDialogViewModel.() -> ConfirmExitDialogViewModel) {
		if (! Platform.isFxApplicationThread()) return runLater { updateOrInvalidated(update) }
		item = item?.update()?.also { isInvalid.set(false) } ?: return invalidate()
	}

	private fun invalidate() {
		isInvalid.set(true)
	}

	override fun updateIf(
		condition: ConfirmExitDialogViewModel.() -> Boolean,
		update: ConfirmExitDialogViewModel.() -> ConfirmExitDialogViewModel
	) {
		threadTransformer.gui {
			if (item.condition()) {
				item = item?.update()
			}
		}
	}
}