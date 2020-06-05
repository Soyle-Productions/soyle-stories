package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.gui.View
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene

class DeleteSceneRamificationsPresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : GetPotentialChangesFromDeletingScene.OutputPort {

	override fun receivePotentialChangesFromDeletingScene(response: GetPotentialChangesFromDeletingScene.ResponseModel) {
		view.update {
			DeleteSceneRamificationsViewModel(
			  "Ok",
			  response.affectedScenes.map {
				  SceneRamificationsViewModel(it.sceneName, it.sceneId.toString(), it.characters.map {
					  CharacterRamificationsViewModel(it.characterName, it.characterId.toString(), it.currentMotivation, it.potentialMotivation)
				  })
			  }
			)
		}
	}

	override fun failedToGetPotentialChangesFromDeletingScene(failure: Exception) {

	}

}