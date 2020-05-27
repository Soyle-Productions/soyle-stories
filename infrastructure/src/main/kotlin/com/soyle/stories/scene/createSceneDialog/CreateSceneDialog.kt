package com.soyle.stories.scene.createSceneDialog

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewListener
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateSceneDialog : Fragment() {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val storyEventId: String? by params

	private val viewListener by resolveLater<CreateNewSceneDialogViewListener>()
	private val model = resolve<CreateSceneDialogModel>()

	override val root = form {
		textfield {
			disableWhen { model.executing }
			requestFocus()
			model.errorMessage.onChange {
				decorators.toList().forEach { removeDecorator(it) }
				if (it != null) {
					addDecorator(SimpleMessageDecorator(it, ValidationSeverity.Error))
				}
			}
			onAction = EventHandler {
				it.consume()
				model.executing.set(true)
				viewListener.createScene(text)
			}
		}
	}

	init {
		titleProperty.bind(model.title)
		model.success.onChange {
			if (it == true) close()
		}
		viewListener.getValidState()
	}

}
fun createSceneDialog(scope: ProjectScope): CreateSceneDialog = scope.get<CreateSceneDialog>().apply {
	openModal(StageStyle.UTILITY, Modality.NONE, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
		centerOnScreen()
		focusedProperty().onChange {
			if (! it) close()
		}
	}
}