package com.soyle.stories.project

import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener

class WorkBenchController(
  private val layoutViewListener: LayoutViewListener
) : WorkBenchViewListener {

	override fun createNewProject() {
		layoutViewListener.openDialog(Dialog.CreateProject)
	}

	override fun createNewCharacter() {
		layoutViewListener.openDialog(Dialog.CreateCharacter)
	}

	override fun createNewLocation() {
		layoutViewListener.openDialog(Dialog.CreateLocation)
	}

	override fun createNewScene() {
		layoutViewListener.openDialog(Dialog.CreateScene)
	}
}