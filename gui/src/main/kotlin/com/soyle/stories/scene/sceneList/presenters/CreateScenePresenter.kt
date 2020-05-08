package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene

class CreateScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : CreateNewScene.OutputPort {

	override fun receiveCreateNewSceneFailure(failure: SceneException) {
	}

	override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes + SceneItemViewModel(response.sceneId.toString(), response.sceneName)
			)
		}
	}

}