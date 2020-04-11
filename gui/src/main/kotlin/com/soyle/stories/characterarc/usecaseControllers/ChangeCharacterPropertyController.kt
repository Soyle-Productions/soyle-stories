package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import java.util.*

class ChangeCharacterPropertyController(
  themeId: String,
  private val changeCharacterPropertyValue: ChangeCharacterPropertyValue,
  private val changeCharacterPropertyValueOutputPort: ChangeCharacterPropertyValue.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun changeCharacterPropertyValue(
	  characterId: String,
	  property: String,
	  value: String
	) {
		changeCharacterPropertyValue.invoke(
		  ChangeCharacterPropertyValue.RequestModel(
			themeId,
			UUID.fromString(characterId),
			ChangeCharacterPropertyValue.Property.valueOf(property),
			value
		  ),
		  changeCharacterPropertyValueOutputPort
		)
	}
}