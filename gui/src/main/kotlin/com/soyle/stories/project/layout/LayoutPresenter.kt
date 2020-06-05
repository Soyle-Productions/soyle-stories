package com.soyle.stories.project.layout

import com.soyle.stories.common.Notifier
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import kotlin.reflect.KClass

class LayoutPresenter(
  private val view: LayoutView,
  private val locale: LayoutLocale,
  getSavedLayoutNotifier: Notifier<GetSavedLayout.OutputPort>,
  toggleToolOpenedNotifier: Notifier<ToggleToolOpened.OutputPort>,
  openToolNotifier: Notifier<OpenTool.OutputPort>,
  closeToolNotifier: Notifier<CloseTool.OutputPort>
) : GetSavedLayout.OutputPort,
  ToggleToolOpened.OutputPort,
  CloseTool.OutputPort,
  OpenTool.OutputPort
{

	init {
		getSavedLayoutNotifier.addListener(this)
		toggleToolOpenedNotifier.addListener(this)
		closeToolNotifier.addListener(this)
		openToolNotifier.addListener(this)
	}

	private fun pushLayout(response: GetSavedLayout.ResponseModel)
	{
		val fixedTools = response.fixedTools.associateBy { it.toolType::class }
		view.update {
			copy(
			  staticTools = FixedTool::class.nestedClasses.map {
				  val obj = it.objectInstance as FixedTool
				  if (it in fixedTools) {
					  StaticToolViewModel(obj, true, locale.toolName(obj))
				  } else {
					  StaticToolViewModel(obj, false, locale.toolName(obj))
				  }
			  },
			  primaryWindow = toWindowViewModel(response.windows.find { it.isPrimary }!!, locale),
			  secondaryWindows = response.windows.filterNot { it.isPrimary }.map { toWindowViewModel(it, locale) },
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


	fun displayDialog(dialog: Dialog) {
		view.update {
			copy(
			  openDialogs = openDialogs + (dialog::class to dialog)
			)
		}
	}

	fun removeDialog(dialog: KClass<out Dialog>) {
		view.update {
			copy(
			  openDialogs = openDialogs - dialog
			)
		}
	}

	override fun receiveCloseToolFailure(failure: LayoutException) {}
	override fun failedToToggleToolOpen(failure: Throwable) {}
	override fun receiveOpenToolFailure(failure: Exception) {}

}