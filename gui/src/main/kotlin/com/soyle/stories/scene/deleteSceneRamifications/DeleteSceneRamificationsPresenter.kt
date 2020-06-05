package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteSceneRamifications.presenters.DeleteCharacterPresenter
import com.soyle.stories.scene.deleteSceneRamifications.presenters.DeleteScenePresenter
import com.soyle.stories.scene.deleteSceneRamifications.presenters.Invalidater
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class DeleteSceneRamificationsPresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>,
  sceneDeleted: Notifier<DeleteScene.OutputPort>,
  characterDeleted: Notifier<RemoveCharacterFromStory.OutputPort>,
  characterMotivationSet: Notifier<SetMotivationForCharacterInScene.OutputPort>
) : GetPotentialChangesFromDeletingScene.OutputPort {

	private val subListeners = listOf(
	  DeleteScenePresenter(view) listensTo sceneDeleted,
	  DeleteCharacterPresenter(view) listensTo characterDeleted,
	  Invalidater(view) listensTo characterMotivationSet
	)

	override fun receivePotentialChangesFromDeletingScene(response: GetPotentialChangesFromDeletingScene.ResponseModel) {
		view.update {
			DeleteSceneRamificationsViewModel(
			  false,
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