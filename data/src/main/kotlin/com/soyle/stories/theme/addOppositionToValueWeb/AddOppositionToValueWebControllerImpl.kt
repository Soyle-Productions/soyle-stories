package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId
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

    override fun addOppositionWithCharacter(valueWebId: String, name: String, characterId: String) {
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