package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import java.util.*

class SetMotivationForCharacterInSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val setMotivationForCharacterInScene: SetMotivationForCharacterInScene,
  private val setMotivationForCharacterInSceneOutputPort: SetMotivationForCharacterInScene.OutputPort
) : SetMotivationForCharacterInSceneController {

	override fun setMotivationForCharacter(sceneId: String, characterId: String, motivation: String) {
		val formattedSceneId = formatSceneId(sceneId)
		val formattedCharacterId = formatCharacterId(characterId)
		threadTransformer.async {
			setMotivationForCharacterInScene.invoke(
			  SetMotivationForCharacterInScene.RequestModel(formattedSceneId, formattedCharacterId, motivation, localeManager.getCurrentLocale()),
			  setMotivationForCharacterInSceneOutputPort
			)
		}
	}

	override fun clearMotivationForCharacter(sceneId: String, characterId: String) {
		val formattedSceneId = formatSceneId(sceneId)
		val formattedCharacterId = formatCharacterId(characterId)
		threadTransformer.async {
			setMotivationForCharacterInScene.invoke(
			  SetMotivationForCharacterInScene.RequestModel(formattedSceneId, formattedCharacterId, null, localeManager.getCurrentLocale()),
			  setMotivationForCharacterInSceneOutputPort
			)
		}
	}

	private fun formatSceneId(sceneId: String) = UUID.fromString(sceneId)
	private fun formatCharacterId(characterId: String) = UUID.fromString(characterId)
}