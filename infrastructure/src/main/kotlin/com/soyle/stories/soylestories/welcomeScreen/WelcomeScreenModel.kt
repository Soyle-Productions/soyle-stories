package com.soyle.stories.soylestories.welcomeScreen

import com.soyle.stories.soylestories.ApplicationModel
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
}