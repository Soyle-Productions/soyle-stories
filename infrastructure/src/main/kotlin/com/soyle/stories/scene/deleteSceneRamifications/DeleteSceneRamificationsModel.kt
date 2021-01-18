package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.View
import tornadofx.ItemViewModel
import tornadofx.rebind

class DeleteSceneRamificationsModel : ItemViewModel<DeleteSceneRamificationsViewModel>(), View.Nullable<DeleteSceneRamificationsViewModel> {

	override val scope: DeleteSceneRamificationsScope = super.scope as DeleteSceneRamificationsScope

	val invalid = bind(DeleteSceneRamificationsViewModel::invalid)
	val scenes = bindImmutableList(DeleteSceneRamificationsViewModel::scenes)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override val viewModel: DeleteSceneRamificationsViewModel? = item

	override fun update(update: DeleteSceneRamificationsViewModel?.() -> DeleteSceneRamificationsViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
		}
	}

	override fun updateOrInvalidated(update: DeleteSceneRamificationsViewModel.() -> DeleteSceneRamificationsViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
		}
	}

}