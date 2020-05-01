package com.soyle.stories.soylestories

import com.soyle.stories.common.async
import com.soyle.stories.di.resolve
import com.soyle.stories.project.projectList.ProjectListViewListener
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.View
import tornadofx.alert
import tornadofx.onChange
import tornadofx.region


class OpenProjectOptionDialog : View("Open Project Options") {

	override val scope: ApplicationScope = super.scope as ApplicationScope

	private val model = find<ApplicationModel>()
	private val projectListViewListener = resolve<ProjectListViewListener>()

	override val root: Parent = region()

	private var alert: Alert? = null


	init {
		model.isOpenProjectOptionsDialogOpen.onChange {
			if (it == true) {
				alert = alert(
				  type = Alert.AlertType.CONFIRMATION,
				  title = "Open Project Options",
				  header = "How would you like to open the project?",
				  owner = currentWindow,
				  buttons = *arrayOf(
					ButtonType("This Window", ButtonBar.ButtonData.YES),
					ButtonType("New Window", ButtonBar.ButtonData.NO),
					ButtonType.CANCEL
				  )
				) {
					val projectLocation = model.openProjectRequest.value!!.location
					when (it.buttonData) {
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
			} else {
				alert?.close()
			}
		}
	}

}