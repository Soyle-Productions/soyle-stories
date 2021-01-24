package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel

internal class DeleteCharacterPresenter(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : RemovedCharacterReceiver {

	override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
		val characterId = characterRemoved.characterId.toString()
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

}