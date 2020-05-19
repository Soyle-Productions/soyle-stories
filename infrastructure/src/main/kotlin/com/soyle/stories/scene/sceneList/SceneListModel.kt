package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.rebind

class SceneListModel : ItemViewModel<SceneListViewModel>(), View.Nullable<SceneListViewModel> {

	override val scope: ProjectScope = super.scope as ProjectScope

	val toolTitle = bind(SceneListViewModel::toolTitle)
	val emptyLabel = bind(SceneListViewModel::emptyLabel)
	val createSceneButtonLabel = bind(SceneListViewModel::createSceneButtonLabel)
	val scenes = bindImmutableList(SceneListViewModel::scenes)
	val hasScenes = bind(SceneListViewModel::hasScenes)
	val renameSceneFailureMessage = bind(SceneListViewModel::renameSceneFailureMessage)

	val selectedItem = SimpleObjectProperty<SceneItemViewModel?>(null)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override fun update(update: SceneListViewModel?.() -> SceneListViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
		}
	}

	override fun updateOrInvalidated(update: SceneListViewModel.() -> SceneListViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
		}
	}
}