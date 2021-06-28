package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class SetMotivationForCharacterInSceneNotifier(
	private val threadTransformer: ThreadTransformer
) :
  Notifier<SetMotivationForCharacterInScene.OutputPort>(),
  SetMotivationForCharacterInScene.OutputPort
{

	override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.motivationSetForCharacterInScene(response) }
		}
	}

	override fun failedToSetMotivationForCharacterInScene(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToSetMotivationForCharacterInScene(failure) }
		}
	}

}