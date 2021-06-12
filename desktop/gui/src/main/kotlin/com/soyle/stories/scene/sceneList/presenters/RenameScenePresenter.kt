package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.renameScene.RenameScene

class RenameScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : SceneRenamedReceiver {

	override suspend fun receiveSceneRenamed(event: SceneRenamed) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes.map {
				  if (it.id == event.sceneId.uuid.toString()) {
				  	it.copy(name = event.sceneName)
				  } else it
			  },
			  renameSceneFailureMessage = null
			)
		}
	}
}