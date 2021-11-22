package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.gui.View
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import com.soyle.stories.scene.sceneList.SceneListViewModel

class DeleteScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : SceneDeletedReceiver {

	override suspend fun receiveSceneDeleted(event: SceneRemoved) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes
				  .asSequence()
				  .filterNot { it.id == event.sceneId }
				  .mapIndexed { index, sceneItemViewModel ->
					  sceneItemViewModel.copy(index = index)
				  }
				  .toList()
			)
		}
	}
}