package com.soyle.stories.scene.sceneList

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.presenters.CreateScenePresenter
import com.soyle.stories.scene.sceneList.presenters.DeleteScenePresenter
import com.soyle.stories.scene.sceneList.presenters.RenameScenePresenter
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.renameScene.RenameScene

class SceneListPresenter(
  private val view: View.Nullable<SceneListViewModel>,
  createSceneNotifier: Notifier<CreateNewScene.OutputPort>,
  renameSceneNotifier: Notifier<RenameScene.OutputPort>,
  deleteSceneNotifier: Notifier<DeleteScene.OutputPort>
) : ListAllScenes.OutputPort {

	private val subPresenters = listOf(
	  CreateScenePresenter(view) listensTo createSceneNotifier,
	  RenameScenePresenter(view) listensTo renameSceneNotifier,
	  DeleteScenePresenter(view) listensTo deleteSceneNotifier
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