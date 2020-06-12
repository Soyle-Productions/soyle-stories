package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.presenters.CreateScenePresenter
import com.soyle.stories.scene.sceneList.presenters.DeleteScenePresenter
import com.soyle.stories.scene.sceneList.presenters.RenameScenePresenter
import com.soyle.stories.scene.sceneList.presenters.ReorderScenePresenter
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene

class SceneListPresenter(
  private val view: View.Nullable<SceneListViewModel>,
  createSceneNotifier: Notifier<CreateNewScene.OutputPort>,
  renameSceneNotifier: Notifier<RenameScene.OutputPort>,
  deleteSceneNotifier: Notifier<DeleteScene.OutputPort>,
  sceneReordered: Notifier<ReorderScene.OutputPort>
) : ListAllScenes.OutputPort {

	private val subPresenters = listOf(
	  CreateScenePresenter(view) listensTo createSceneNotifier,
	  RenameScenePresenter(view) listensTo renameSceneNotifier,
	  DeleteScenePresenter(view) listensTo deleteSceneNotifier,
	  ReorderScenePresenter(view) listensTo sceneReordered
	)

	override fun receiveListAllScenesResponse(response: ListAllScenes.ResponseModel) {
		view.update {
			SceneListViewModel(
			  toolTitle = "Scenes",
			  emptyLabel = "No Scenes to display",
			  createSceneButtonLabel = "Create New Scene",
			  scenes = response.scenes.map(::SceneItemViewModel),
			  renameSceneFailureMessage = null
			)
		}
	}

}