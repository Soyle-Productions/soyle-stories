package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import java.util.*

class ChangeCharacterPerspectivePropertyController(
  private val themeId: String,
  private val changeCharacterPerspectivePropertyValue: ChangeCharacterPerspectivePropertyValue,
  private val changeCharacterPerspectivePropertyValueOutputPort: ChangeCharacterPerspectivePropertyValue.OutputPort
) {

	suspend fun changeSharedPropertyValue(
	  perspectiveCharacterId: String,
	  targetCharacterId: String,
	  property: String,
	  value: String
	) {
		changeCharacterPerspectivePropertyValue.invoke(
		  ChangeCharacterPerspectivePropertyValue.RequestModel(
			UUID.fromString(themeId),
			UUID.fromString(perspectiveCharacterId),
			UUID.fromString(targetCharacterId),
			ChangeCharacterPerspectivePropertyValue.Property.valueOf(property),
			value
		  ),
		  changeCharacterPerspectivePropertyValueOutputPort
		)
	}

}