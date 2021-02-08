package com.soyle.stories.storyevent.createStoryEventDialog

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateStoryEventDialog : Fragment() {

	override val scope: ProjectScope = super.scope as ProjectScope

	internal val relativeStoryEventId: String? by params
	internal val isBefore: Boolean? by params

	private val viewListener = resolve<CreateStoryEventDialogViewListener>()
	private val model = resolve<CreateStoryEventDialogModel>()

	override val root: Parent = form {
		textfield {
			requestFocus()
			model.errorMessage.onChange {
				decorators.toList().forEach { removeDecorator(it) }
				if (it != null) {
					addDecorator(SimpleMessageDecorator(it, ValidationSeverity.Error))
				}
			}
			onAction = EventHandler {
				it.consume()
				val nonBlankName = NonBlankString.create(text)
				if (nonBlankName != null) {
					model.isExecuting.set(true)
					viewListener.createStoryEvent(
						nonBlankName, relativeStoryEventId,
						when (isBefore) {
							null -> null
							true -> "before"
							else -> "after"
						}
					)
				} else {
					model.errorMessage.value = "Name cannot be blank"
				}
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

fun createStoryEventDialog(scope: ProjectScope): CreateStoryEventDialog = find<CreateStoryEventDialog>(scope,
  mapOf(CreateStoryEventDialog::relativeStoryEventId to null, CreateStoryEventDialog::isBefore to null)
).apply {
	openModal(StageStyle.UTILITY, Modality.NONE, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
		centerOnScreen()
		focusedProperty().onChange {
			if (!it) close()
		}
	}
}

fun createStoryEventDialog(scope: ProjectScope, relativeStoryEventId: String, isBefore: Boolean): CreateStoryEventDialog = find<CreateStoryEventDialog>(scope,
  mapOf(CreateStoryEventDialog::relativeStoryEventId to relativeStoryEventId, CreateStoryEventDialog::isBefore to isBefore)
).apply {
	openModal(StageStyle.UTILITY, Modality.NONE, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
		centerOnScreen()
		focusedProperty().onChange {
			if (!it) close()
		}
	}
}