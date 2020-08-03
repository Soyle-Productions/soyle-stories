package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangeCharacterDesire
import java.util.*

class ChangeSectionValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCharacterDesire: ChangeCharacterDesire,
    private val changeCharacterDesireOutputPort: ChangeCharacterDesire.OutputPort
) : ChangeSectionValueController {

    override fun changeDesire(themeId: String, characterId: String, desire: String) {
        val request = ChangeCharacterDesire.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            desire
        )
        threadTransformer.async {
            changeCharacterDesire.invoke(
                request, changeCharacterDesireOutputPort
            )
        }
    }
}