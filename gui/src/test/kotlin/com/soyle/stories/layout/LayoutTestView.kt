package com.soyle.stories.layout

import com.soyle.stories.project.layout.*
import kotlinx.coroutines.runBlocking
import java.util.*

class LayoutTestView(
  private val projectId: UUID,
  private val layoutViewListener: LayoutViewListener,
  layoutModel: () -> LayoutViewModelWrapper
) {

	private val model by lazy(layoutModel)

	init {
		runBlocking {
			layoutViewListener.loadLayoutForProject(projectId)
		}
	}

	fun selectMenuItem(menuItemKey: String) {
		val hierarchy = menuItemKey.split(".")
		when (hierarchy[0]) {
			"File" -> when (hierarchy[1]) {
				"New" -> when (hierarchy[2]) {
					"Project" -> {}
					"Character" -> {}
					"Location" -> {
						layoutViewListener.openDialog(Dialog.CreateLocation)
					}
				}
			}
			"Tools" -> when (hierarchy[1]) {
				"Characters" -> {}
				"Locations" -> runBlocking { layoutViewListener.toggleToolOpen(model.staticTools.find { it.name == "Locations" }!!.toolId) }
			}
		}
	}
}