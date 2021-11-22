package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.gui.View
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel

internal class DeleteScenePresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : SceneDeletedReceiver {

	override suspend fun receiveSceneDeleted(event: SceneRemoved) {
		view.updateOrInvalidated {
			if (scenes.find { it.sceneId == event.sceneId.uuid.toString() } == null) return@updateOrInvalidated this
			copy(
			  scenes = scenes.filterNot {
				  it.sceneId == event.sceneId.uuid.toString()
			  }
			)
		}
	}
}
