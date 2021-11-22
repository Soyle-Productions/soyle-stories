package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.emptyProperty
import com.soyle.stories.di.resolve
import com.soyle.stories.scene.delete.DeleteSceneRamificationsReport
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class DeleteSceneRamificationsReportView(
	private val viewModel: DeleteSceneRamificationsReportViewModel
) : View() {

	override val root: Parent = vbox {
		stackpane {
			vgrow = Priority.ALWAYS
			emptyListDisplay(
				viewModel.affectedScenes.emptyProperty().not(),
				"No scenes will be affected by deleting this scene.".toProperty(),
				"Delete Scene".toProperty(),
				callToAction = viewModel::delete
			)
			vbox {
				visibleWhen { viewModel.affectedScenes.emptyProperty() }
				managedProperty().bind(visibleProperty())
				vgrow = Priority.ALWAYS
				bindChildren(viewModel.affectedScenes) {
					sceneItem(it)
				}
			}
		}
		buttonbar {
			this.padding = Insets(10.0, 10.0, 10.0, 10.0)
			button("Delete") {
				action(viewModel::delete)
			}
			button("Cancel") {
				action(viewModel::cancel)
			}
		}
	}

	init {
		root.properties[UI_COMPONENT_PROPERTY] = this
		FX.getComponents(scope)[this::class] = this
	}
}