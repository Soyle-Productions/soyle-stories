package com.soyle.stories.project.layout

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.project.layout.config.RegisteredToolsConfig
import kotlin.reflect.KClass

class LayoutPresenter(
	private val view: View.Nullable<LayoutViewModel>,
	getSavedLayoutNotifier: Notifier<GetSavedLayout.OutputPort>,
	toggleToolOpenedNotifier: Notifier<ToggleToolOpened.OutputPort>,
	openToolNotifier: Notifier<OpenTool.OutputPort>,
	closeToolNotifier: Notifier<CloseTool.OutputPort>,
	removeToolsWithIdNotifier: Notifier<RemoveToolsWithId.OutputPort>,
	private val registeredTools: RegisteredToolsConfig
) : GetSavedLayout.OutputPort,
  ToggleToolOpened.OutputPort,
  CloseTool.OutputPort,
  OpenTool.OutputPort,
RemoveToolsWithId.OutputPort {

	init {
		getSavedLayoutNotifier.addListener(this)
		toggleToolOpenedNotifier.addListener(this)
		closeToolNotifier.addListener(this)
		openToolNotifier.addListener(this)
		this listensTo removeToolsWithIdNotifier
	}

	private fun pushLayout(response: GetSavedLayout.ResponseModel) {
		val openFixedTools = response.fixedTools.map { it.toolType }.toSet()
		view.update {
			LayoutViewModel(
			  staticTools = registeredTools.listFixedToolTypes().map { fixedTool ->
				  StaticToolViewModel(
					fixedTool,
					fixedTool in openFixedTools,
					registeredTools.getConfigFor(fixedTool).toolName()
				  )
			  },
			  primaryWindow = toWindowViewModel(response.windows.find { it.isPrimary }!!, registeredTools),
			  secondaryWindows = response.windows.filterNot { it.isPrimary }.map { toWindowViewModel(it, registeredTools) },
			  isValid = true
			)
		}
	}

	override fun receiveGetSavedLayoutResponse(response: GetSavedLayout.ResponseModel) {
		pushLayout(response)
	}

	override fun receiveToggleToolOpenedResponse(response: GetSavedLayout.ResponseModel) {
		pushLayout(response)
	}

	override fun receiveCloseToolResponse(response: GetSavedLayout.ResponseModel) {
		pushLayout(response)
	}

	override fun receiveOpenToolResponse(response: GetSavedLayout.ResponseModel) {
		pushLayout(response)
	}

	override fun toolsRemovedWithId(response: GetSavedLayout.ResponseModel) {
		pushLayout(response)
	}


	fun displayDialog(dialog: Dialog) {
		view.updateOrInvalidated {
			copy(
			  openDialogs = openDialogs + (dialog::class to dialog)
			)
		}
	}

	fun removeDialog(dialog: KClass<out Dialog>) {
		view.updateOrInvalidated {
			copy(
			  openDialogs = openDialogs - dialog
			)
		}
	}

	override fun receiveCloseToolFailure(failure: LayoutException) {}
	override fun failedToToggleToolOpen(failure: Throwable) {}
	override fun receiveOpenToolFailure(failure: Exception) {}
	override fun failedToRemoveToolsWithId(failure: Exception) {}

}