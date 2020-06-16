package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.di.resolve
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class DeleteSceneRamifications : View() {

	override val scope: DeleteSceneRamificationsScope = super.scope as DeleteSceneRamificationsScope
	private val viewListener = resolve<DeleteSceneRamificationsViewListener>()
	private val model = resolve<DeleteSceneRamificationsModel>()

	override val root: Parent = vbox {
		stackpane {
			vgrow = Priority.ALWAYS
			emptyListDisplay(
				model.scenes.select { it.isNotEmpty().toProperty() },
				"No scenes will be affected by deleting this scene.".toProperty(),
				"Delete Scene".toProperty()
			) {
				viewListener.deleteScene(scope.sceneId)
			}
			vbox {
				visibleWhen { model.scenes.select { it.isNotEmpty().toProperty() } }
				managedProperty().bind(visibleProperty())
				vgrow = Priority.ALWAYS
				bindChildren(model.scenes) {
					sceneItem(it)
				}
			}
		}
		buttonbar {
			button("Delete") {
				action {
					viewListener.deleteScene(scope.sceneId)
				}
			}
			button("Cancel") {
				action {
					owningTab?.onCloseRequest?.handle(ActionEvent())
				}
			}
		}
	}

	init {
		model.invalid.onChange {
			if (it != false) {
				viewListener.getValidState()
			}
		}
		viewListener.getValidState()
	}
}