package com.soyle.stories.storyevent.createStoryEventDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel
import tornadofx.rebind

class CreateStoryEventDialogModel : View.Nullable<CreateStoryEventDialogViewModel>, ItemViewModel<CreateStoryEventDialogViewModel>() {

	override val scope: ProjectScope = super.scope as ProjectScope

	val title = bind(CreateStoryEventDialogViewModel::title)
	val errorMessage = bind(CreateStoryEventDialogViewModel::errorMessage)
	val success = bind(CreateStoryEventDialogViewModel::success)

	val isExecuting = SimpleBooleanProperty(false)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override fun update(update: CreateStoryEventDialogViewModel?.() -> CreateStoryEventDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
		}
	}

	override fun updateOrInvalidated(update: CreateStoryEventDialogViewModel.() -> CreateStoryEventDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
		}
	}
}