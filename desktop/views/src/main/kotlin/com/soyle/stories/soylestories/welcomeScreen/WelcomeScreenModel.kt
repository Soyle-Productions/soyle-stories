package com.soyle.stories.soylestories.welcomeScreen

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.confirmExitDialog.ConfirmExitDialogViewModel
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel
import tornadofx.runLater

class WelcomeScreenModel : WelcomeScreenView, ItemViewModel<WelcomeScreenViewModel>() {

	val title = bind(WelcomeScreenViewModel::title)
	val applicationName = bind(WelcomeScreenViewModel::applicationName)
	val createNewProjectButton = bind(WelcomeScreenViewModel::createNewProjectButton)
	val openProjectButton = bind(WelcomeScreenViewModel::openProjectButton)
	val isOpen = find<ApplicationModel>().isWelcomeScreenVisible

	val isInvalid = SimpleBooleanProperty(true)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope as ApplicationScope)

	override val viewModel: WelcomeScreenViewModel? = item

	override fun update(update: WelcomeScreenViewModel?.() -> WelcomeScreenViewModel) {
		if (! Platform.isFxApplicationThread()) return runLater { update(update) }
		isInvalid.set(false)
		item = item.update()
	}

	override fun updateOrInvalidated(update: WelcomeScreenViewModel.() -> WelcomeScreenViewModel) {
		if (! Platform.isFxApplicationThread()) return runLater { updateOrInvalidated(update) }
		item = item?.update()?.also { isInvalid.set(false) } ?: return invalidate()
	}

	private fun invalidate() {
		isInvalid.set(true)
	}

	override fun updateIf(
		condition: WelcomeScreenViewModel.() -> Boolean,
		update: WelcomeScreenViewModel.() -> WelcomeScreenViewModel
	) {
		threadTransformer.gui {
			if (item.condition()) {
				item = item?.update()
			}
		}
	}
}