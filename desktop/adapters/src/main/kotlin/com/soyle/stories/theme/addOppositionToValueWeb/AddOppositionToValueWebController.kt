package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.domain.validation.NonBlankString

interface AddOppositionToValueWebController {

    fun addOpposition(valueWebId: String)
    fun addOppositionWithCharacter(valueWebId: String, name: NonBlankString, characterId: String)

}