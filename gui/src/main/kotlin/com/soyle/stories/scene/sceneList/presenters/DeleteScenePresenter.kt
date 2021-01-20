package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene

class DeleteScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : DeleteScene.OutputPort {

	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes
				  .asSequence()
				  .filterNot { it.id == responseModel.sceneId.toString() }
				  .mapIndexed { index, sceneItemViewModel ->
					  sceneItemViewModel.copy(index = index)
				  }
				  .toList()
			)
		}
	}

	override fun receiveDeleteSceneFailure(failure: SceneException) {}
}