package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import java.util.*

class AddOppositionToValueWebControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addOppositionToValueWeb: AddOppositionToValueWeb,
    private val addOppositionToValueWebOutputPort: AddOppositionToValueWeb.OutputPort
) : AddOppositionToValueWebController {

    override fun addOpposition(valueWebId: String) {
        val request = RequestModel(
            UUID.fromString(valueWebId)
        )
        addOppositionToValueWeb(request)
    }

    override fun addOppositionWithCharacter(valueWebId: String, name: NonBlankString, characterId: String) {
        val request = RequestModel(
            UUID.fromString(valueWebId),
            name,
            CharacterId(UUID.fromString(characterId))
        )
        addOppositionToValueWeb(request)
    }

    private fun addOppositionToValueWeb(requestModel: RequestModel)
    {
        threadTransformer.async {
            addOppositionToValueWeb.invoke(
                requestModel, addOppositionToValueWebOutputPort
            )
        }
    }

}