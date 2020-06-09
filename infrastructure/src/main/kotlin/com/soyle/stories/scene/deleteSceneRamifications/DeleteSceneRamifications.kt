package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.di.resolve
import javafx.scene.Parent
import tornadofx.*

class DeleteSceneRamifications : View() {

	override val scope: DeleteSceneRamificationsScope = super.scope as DeleteSceneRamificationsScope
	private val viewListener = resolve<DeleteSceneRamificationsViewListener>()
	private val model = resolve<DeleteSceneRamificationsModel>()

	override val root: Parent = stackpane {
		pane {
			addClass("ok")
			label("All good!")
		}
		vbox {
			bindChildren(model.scenes) {
				sceneItem(it)
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