package com.soyle.stories.scene.createSceneDialog

import com.soyle.stories.common.async
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateSceneDialog : Fragment("Create New Scene") {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val errorMessage = SimpleStringProperty("")

	override val root = form {
		textfield {
			requestFocus()
			onAction = EventHandler {
				it.consume()
				if (text.isEmpty())
				{
					val errorDecorator = SimpleMessageDecorator("Name cannot be blank", ValidationSeverity.Error)
					decorators.toList().forEach { removeDecorator(it) }
					addDecorator(errorDecorator)
					return@EventHandler
				}
				async(scope) {
				}
				close()
			}
		}
	}

}
fun createSceneDialog(scope: ProjectScope): CreateSceneDialog = scope.get<CreateSceneDialog>().apply {
	openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
		centerOnScreen()
	}
}