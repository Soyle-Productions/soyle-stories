package com.soyle.stories.theme.changeCharacterPerspectiveProperty

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import java.util.*
import kotlin.concurrent.thread

class ChangeCharacterPerspectivePropertyControllerImpl(
	private val threadTransformer: ThreadTransformer,
  private val changeCharacterPerspectivePropertyValue: ChangeCharacterPerspectivePropertyValue,
  private val changeCharacterPerspectivePropertyValueOutputPort: ChangeCharacterPerspectivePropertyValue.OutputPort
) : ChangeCharacterPerspectivePropertyController {

	override fun setAttackByOpponent(
		themeId: String,
		perspectiveCharacterId: String,
		opponentId: String,
		attack: String
	) {
		val request = ChangeCharacterPerspectivePropertyValue.RequestModel(
			UUID.fromString(themeId),
			UUID.fromString(perspectiveCharacterId),
			UUID.fromString(opponentId),
			ChangeCharacterPerspectivePropertyValue.Property.Attack,
			attack
		)
		threadTransformer.async {
			changeCharacterPerspectivePropertyValue.invoke(
				request, changeCharacterPerspectivePropertyValueOutputPort
			)
		}
	}

	override fun setSimilaritiesBetweenCharacters(
		themeId: String,
		perspectiveCharacterId: String,
		opponentId: String,
		similarities: String
	) {
		val request = ChangeCharacterPerspectivePropertyValue.RequestModel(
			UUID.fromString(themeId),
			UUID.fromString(perspectiveCharacterId),
			UUID.fromString(opponentId),
			ChangeCharacterPerspectivePropertyValue.Property.Similarities,
			similarities
		)
		threadTransformer.async {
			changeCharacterPerspectivePropertyValue.invoke(
				request, changeCharacterPerspectivePropertyValueOutputPort
			)
		}
	}

}