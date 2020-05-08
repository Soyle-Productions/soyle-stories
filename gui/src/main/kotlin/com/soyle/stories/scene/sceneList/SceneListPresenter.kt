package com.soyle.stories.scene.sceneList

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.presenters.CreateScenePresenter
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes

class SceneListPresenter(
  private val view: View.Nullable<SceneListViewModel>,
  createSceneNotifier: Notifier<CreateNewScene.OutputPort>
) : ListAllScenes.OutputPort {

	private val subPresenters = listOf(
	  CreateScenePresenter(view) listensTo createSceneNotifier
	)

	override fun receiveListAllScenesResponse(response: ListAllScenes.ResponseModel) {
		view.update {
			SceneListViewModel(
			  toolTitle = "Scenes",
			  emptyLabel = "No Scenes to display",
			  createSceneButtonLabel = "Create New Scene",
			  scenes = response.scenes.map(::SceneItemViewModel)
			)
		}
	}

}