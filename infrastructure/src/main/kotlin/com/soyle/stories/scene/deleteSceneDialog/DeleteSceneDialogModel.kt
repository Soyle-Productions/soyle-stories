package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import tornadofx.ItemViewModel
import tornadofx.rebind

class DeleteSceneDialogModel : ItemViewModel<DeleteSceneDialogViewModel>(), View.Nullable<DeleteSceneDialogViewModel> {

	override val scope: ProjectScope = super.scope as ProjectScope

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override fun update(update: DeleteSceneDialogViewModel?.() -> DeleteSceneDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
		}
	}

	override fun updateOrInvalidated(update: DeleteSceneDialogViewModel.() -> DeleteSceneDialogViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
		}
	}

}