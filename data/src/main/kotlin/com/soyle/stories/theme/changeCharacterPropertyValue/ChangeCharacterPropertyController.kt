package com.soyle.stories.theme.changeCharacterPropertyValue

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue

interface ChangeCharacterPropertyController {

    fun setArchetype(themeId: String, characterId: String, archetype: String)

}