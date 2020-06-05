package com.soyle.stories.scene.deleteSceneRamifications.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class Invalidater(
  private val view: View.Nullable<DeleteSceneRamificationsViewModel>
) : SetMotivationForCharacterInScene.OutputPort {

	override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
		view.update {
			DeleteSceneRamificationsViewModel(invalid = true, okMessage = "", scenes = emptyList())
		}
	}

	override fun failedToSetMotivationForCharacterInScene(failure: Exception) {}

}