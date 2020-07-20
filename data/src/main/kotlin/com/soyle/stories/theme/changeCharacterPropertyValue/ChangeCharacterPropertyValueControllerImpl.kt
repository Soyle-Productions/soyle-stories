package com.soyle.stories.theme.changeCharacterPropertyValue

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import java.util.*

class ChangeCharacterPropertyValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCharacterPropertyValue: ChangeCharacterPropertyValue,
    private val changeCharacterPropertyValueOutputPort: ChangeCharacterPropertyValue.OutputPort
) : ChangeCharacterPropertyController {

    override fun setArchetype(themeId: String, characterId: String, archetype: String) {
        val preparedThemeId = UUID.fromString(themeId)
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            changeCharacterPropertyValue.invoke(
                ChangeCharacterPropertyValue.RequestModel(
                    preparedThemeId,
                    preparedCharacterId,
                    ChangeCharacterPropertyValue.Property.Archetype,
                    archetype
                ),
                changeCharacterPropertyValueOutputPort
            )
        }
    }


}