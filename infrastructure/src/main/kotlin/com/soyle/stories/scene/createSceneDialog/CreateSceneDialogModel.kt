package com.soyle.stories.scene.createSceneDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewModel
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel
import tornadofx.rebind

class CreateSceneDialogModel : ItemViewModel<CreateNewSceneDialogViewModel>(), View.Nullable<CreateNewSceneDialogViewModel> {

	override val scope: ProjectScope = super.scope as ProjectScope

	val title = bind(CreateNewSceneDialogViewModel::title)
	val nameLabel = bind(CreateNewSceneDialogViewModel::nameLabel)
	val errorMessage = bind(CreateNewSceneDialogViewModel::errorMessage)
	val success = bind(CreateNewSceneDialogViewModel::success)

	val executing = SimpleBooleanProperty(false)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override fun update(update: CreateNewSceneDialogViewModel?.() -> CreateNewSceneDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
			executing.set(false)
		}
	}

	override fun updateOrInvalidated(update: CreateNewSceneDialogViewModel.() -> CreateNewSceneDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
			executing.set(false)
		}
	}

}