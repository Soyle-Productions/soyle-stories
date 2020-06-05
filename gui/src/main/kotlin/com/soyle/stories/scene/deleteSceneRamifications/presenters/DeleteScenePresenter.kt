package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene

internal class DeleteScenePresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : DeleteScene.OutputPort {

	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		val sceneId = responseModel.sceneId.toString()
		view.updateOrInvalidated {
			if (scenes.find { it.sceneId == sceneId } == null) return@updateOrInvalidated this
			copy(
			  scenes = scenes.filterNot {
				  it.sceneId == sceneId
			  }
			)
		}
	}

	override fun receiveDeleteSceneFailure(failure: SceneException) {}
}