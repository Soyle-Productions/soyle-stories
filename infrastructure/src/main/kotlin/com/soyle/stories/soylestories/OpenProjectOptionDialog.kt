package com.soyle.stories.soylestories

import com.soyle.stories.common.async
import com.soyle.stories.di.resolve
import com.soyle.stories.project.projectList.ProjectListViewListener
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*


class OpenProjectOptionDialog : View("Open Project Options") {

	override val scope: ApplicationScope = super.scope as ApplicationScope

	private val model = find<ApplicationModel>()
	private val projectListViewListener = resolve<ProjectListViewListener>()

	override val root: Parent = vbox {
		label("How would you like to open the project?")
		hbox(alignment = Pos.CENTER_RIGHT, spacing = 5) {
			button("This Window") {
				action {
					val projectLocation = model.openProjectRequest.value!!.location
					async(scope) {
						projectListViewListener.replaceCurrentProject(projectLocation)
					}
				}
			}
			button("New Window") {
				action {
					val projectLocation = model.openProjectRequest.value!!.location
					async(scope) {
						projectListViewListener.forceOpenProject(projectLocation)
					}
				}
			}
			button("Cancel") {
				isCancelButton = true
				action {
					model.openProjectRequest.value = null
					close()
				}
			}
		}
	}


	init {
		model.isOpenProjectOptionsDialogOpen.onChange {
			if (it == true) {
				openModal(
				  StageStyle.UTILITY,
				  Modality.APPLICATION_MODAL,
				  escapeClosesWindow = false,
				  owner = null,
				  block = true,
				  resizable = true
				)
			} else {
				close()
			}
		}
	}

}