package com.soyle.stories.soylestories

import com.soyle.stories.common.async
import com.soyle.stories.di.resolve
import com.soyle.stories.project.projectList.ProjectListViewListener
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import tornadofx.View
import tornadofx.onChange
import tornadofx.onChangeOnce
import tornadofx.region


class OpenProjectOptionDialog : View("Open Project Options") {

	override val scope: ApplicationScope = super.scope as ApplicationScope

	private val model = find<ApplicationModel>()
	private val projectListViewListener = resolve<ProjectListViewListener>()

	override val root: Parent = region()

	private fun alert(): Alert = Alert(Alert.AlertType.CONFIRMATION, "",
		ButtonType("This Window", ButtonBar.ButtonData.YES),
		ButtonType("New Window", ButtonBar.ButtonData.NO),
		ButtonType.CANCEL
	).apply {
		title = "Open Project Options"
		headerText = "How would you like to open the project?"
	}
	private var previousAlert: Alert? = null

	init {
		model.isOpenProjectOptionsDialogOpen.onChange { isOpen ->
			if (isOpen) {
				val projectLocation = model.openProjectRequest.value!!.location
				val alert = alert()
				alert.initModality(Modality.APPLICATION_MODAL)
				alert.initOwner(currentWindow)
				alert.resultProperty().onChangeOnce {
					when (it?.buttonData) {
						ButtonBar.ButtonData.YES -> {
							async(scope) {
								projectListViewListener.replaceCurrentProject(projectLocation)
							}
						}
						ButtonBar.ButtonData.NO -> {
							async(scope) {
								projectListViewListener.forceOpenProject(projectLocation)
							}
						}
						ButtonBar.ButtonData.CANCEL_CLOSE -> {
							model.openProjectRequest.value = null
						}
						else -> {}
					}
				}
				previousAlert = alert
				alert.show()
			} else {
				previousAlert?.close()
				previousAlert = null
			}
		}
	}

}