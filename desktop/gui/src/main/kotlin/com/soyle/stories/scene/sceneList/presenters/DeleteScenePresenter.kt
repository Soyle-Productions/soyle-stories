package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteScene.SceneDeletedReceiver
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene

class DeleteScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : SceneDeletedReceiver {

	override suspend fun receiveSceneDeleted(event: Scene.Id) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes
				  .asSequence()
				  .filterNot { it.id == event.uuid.toString() }
				  .mapIndexed { index, sceneItemViewModel ->
					  sceneItemViewModel.copy(index = index)
				  }
				  .toList()
			)
		}
	}
}