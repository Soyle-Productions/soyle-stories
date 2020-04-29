package com.soyle.stories.project.layout.openTool

import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.containsGroup
import com.soyle.stories.project.layout.updateGroup

internal class OpenToolPresenter(
  private val view: LayoutView
) : OpenTool.OutputPort {

	override fun receiveOpenToolFailure(failure: Exception) {}

	override fun receiveOpenToolResponse(response: OpenTool.ResponseModel) {
		view.update {

			val windows = (secondaryWindows + primaryWindow!!)
			val window = windows.find { it.containsGroup(response.affectedToolGroup.groupId) }

			when (window) {
				null -> this
				primaryWindow -> copy(
				  primaryWindow = window.updateGroup(response.affectedToolGroup)
				)
				else -> copy(
				  secondaryWindows = secondaryWindows.filterNot { it.id == window.id } + window.updateGroup(response.affectedToolGroup)
				)
			}
		}
	}

}