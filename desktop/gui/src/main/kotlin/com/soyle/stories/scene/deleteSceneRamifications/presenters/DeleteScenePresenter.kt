package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteScene.SceneDeletedReceiver
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene

internal class DeleteScenePresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : SceneDeletedReceiver {

	override suspend fun receiveSceneDeleted(event: Scene.Id) {
		view.updateOrInvalidated {
			if (scenes.find { it.sceneId == event.uuid.toString() } == null) return@updateOrInvalidated this
			copy(
			  scenes = scenes.filterNot {
				  it.sceneId == event.uuid.toString()
			  }
			)
		}
	}
}
