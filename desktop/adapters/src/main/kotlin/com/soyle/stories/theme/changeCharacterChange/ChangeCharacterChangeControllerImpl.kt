package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeCharacterChange.ChangeCharacterChange
import java.util.*

class ChangeCharacterChangeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCharacterChange: ChangeCharacterChange,
    private val changeCharacterChangeOutputPort: ChangeCharacterChange.OutputPort
) : ChangeCharacterChangeController {

    override fun changeCharacterChange(themeId: String, characterId: String, characterChange: String) {
        val request = ChangeCharacterChange.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            characterChange
        )
        threadTransformer.async {
            changeCharacterChange.invoke(request, changeCharacterChangeOutputPort)
        }
    }

}