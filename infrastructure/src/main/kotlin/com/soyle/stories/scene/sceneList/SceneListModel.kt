package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.beans.property.SimpleObjectProperty

class SceneListModel : ProjectScopedModel<SceneListViewModel>() {

	val toolTitle = bind(SceneListViewModel::toolTitle)
	val emptyLabel = bind(SceneListViewModel::emptyLabel)
	val createSceneButtonLabel = bind(SceneListViewModel::createSceneButtonLabel)
	val scenes = bind(SceneListViewModel::scenes)
	val hasScenes = bind(SceneListViewModel::hasScenes)
	val renameSceneFailureMessage = bind(SceneListViewModel::renameSceneFailureMessage)

	val editingItem = SimpleObjectProperty<SceneItemViewModel?>(null)
	val selectedItem = SimpleObjectProperty<SceneItemViewModel?>(null)
}