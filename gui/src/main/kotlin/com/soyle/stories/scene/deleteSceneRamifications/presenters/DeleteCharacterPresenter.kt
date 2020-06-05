package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel

internal class DeleteCharacterPresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : RemoveCharacterFromStory.OutputPort {

	override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
		val characterId = response.characterId.toString()
		view.updateOrInvalidated {
			copy(
			  scenes = scenes.mapNotNull {
				  it.copy(
					characters = it.characters.filterNot {
						it.characterId == characterId
					}
				  ).takeIf { it.characters.isNotEmpty() }
			  }
			)
		}
	}

	override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}

}