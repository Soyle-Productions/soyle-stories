package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene

class ReorderScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : ReorderScene.OutputPort {

	override fun sceneReordered(response: ReorderScene.ResponseModel) {
		val newIndices = (response.updatedScenes + response.scene).associateBy { it.id.toString() }
		view.updateOrInvalidated {
			copy(
			  scenes = scenes.map {
				  if (it.id in newIndices) {
					  SceneItemViewModel(
						newIndices.getValue(it.id)
					  )
				  }
				  else it
			  }.sortedBy { it.index }
			)
		}
	}

	override fun failedToReorderScene(failure: Exception) {}

}