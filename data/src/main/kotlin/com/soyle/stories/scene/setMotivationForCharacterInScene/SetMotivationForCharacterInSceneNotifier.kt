package com.soyle.stories.scene.setMotivationForCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class SetMotivationForCharacterInSceneNotifier :
  Notifier<SetMotivationForCharacterInScene.OutputPort>(),
  SetMotivationForCharacterInScene.OutputPort
{

	override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
		notifyAll { it.motivationSetForCharacterInScene(response) }
	}

	override fun failedToSetMotivationForCharacterInScene(failure: Exception) {
		notifyAll { it.failedToSetMotivationForCharacterInScene(failure) }
	}

}