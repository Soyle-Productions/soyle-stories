package com.soyle.stories.scene.createSceneDialog

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.di.resolveLater
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewListener
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateSceneDialog : Fragment() {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val sceneId: String? by params
	private val relativeDirection: Boolean by params
	private val onSceneCreated: (Scene.Id) -> Unit by params

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
				val name = NonBlankString.create(text)
				if (name == null) {
					model.errorMessage.value = "Name cannot be blank"
					return@EventHandler
				}
				model.executing.set(true)
				createScene(name)
			}
		}
	}

	private fun createScene(name: NonBlankString) {
		val deferredSceneId = when {
			sceneId != null -> when (relativeDirection) {
				true -> viewListener.createSceneBefore(name, sceneId!!)
				false -> viewListener.createSceneAfter(name, sceneId!!)
			}
			else -> viewListener.createScene(name)
		}
		deferredSceneId.invokeOnCompletion {
			if (it == null) onSceneCreated(deferredSceneId.getCompleted())
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
fun createSceneDialog(
	scope: ProjectScope,
	relativeSceneId: String? = null,
	direction: Boolean = true,
	onSceneCreated: (Scene.Id) -> Unit = {}
): CreateSceneDialog {
	val params = mapOf(
		"sceneId" to relativeSceneId,
		"relativeDirection" to direction,
		"onSceneCreated" to onSceneCreated
	)
	return find<CreateSceneDialog>(scope, params).apply {
		openModal(
			StageStyle.UTILITY,
			Modality.NONE,
			escapeClosesWindow = true,
			owner = scope.get<WorkBench>().currentWindow
		)?.apply {
			centerOnScreen()
			focusedProperty().onChange {
				if (!it) close()
			}
		}
	}
}