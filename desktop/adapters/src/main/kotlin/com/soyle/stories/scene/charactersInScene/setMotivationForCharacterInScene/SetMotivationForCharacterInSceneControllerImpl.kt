package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import java.util.*

class SetMotivationForCharacterInSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val setMotivationForCharacterInScene: SetMotivationForCharacterInScene,
  private val setMotivationForCharacterInSceneOutputPort: SetMotivationForCharacterInScene.OutputPort
) : SetMotivationForCharacterInSceneController {

	override fun setMotivationForCharacter(sceneId: Scene.Id, characterId: Character.Id, motivation: String) {
		threadTransformer.async {
			setMotivationForCharacterInScene.invoke(
			  SetMotivationForCharacterInScene.RequestModel(sceneId, characterId, motivation),
			  setMotivationForCharacterInSceneOutputPort
			)
		}
	}

	override fun clearMotivationForCharacter(sceneId: Scene.Id, characterId: Character.Id) {
		threadTransformer.async {
			setMotivationForCharacterInScene.invoke(
			  SetMotivationForCharacterInScene.RequestModel(sceneId, characterId, null),
			  setMotivationForCharacterInSceneOutputPort
			)
		}
	}

	private fun formatSceneId(sceneId: String) = UUID.fromString(sceneId)
	private fun formatCharacterId(characterId: String) = UUID.fromString(characterId)
}