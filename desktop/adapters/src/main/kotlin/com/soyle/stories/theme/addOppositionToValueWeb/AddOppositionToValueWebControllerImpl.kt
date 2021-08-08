package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.util.*

class AddOppositionToValueWebControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addOppositionToValueWeb: AddOppositionToValueWeb,
    private val addOppositionToValueWebOutputPort: AddOppositionToValueWeb.OutputPort
) : AddOppositionToValueWebController {

    override fun addOpposition(valueWebId: String): Deferred<OppositionAddedToValueWeb> {
        val request = RequestModel(
            UUID.fromString(valueWebId)
        )
        return addOppositionToValueWeb(request)
    }

    override fun addOpposition(valueWebId: String, name: NonBlankString): Deferred<OppositionAddedToValueWeb> {
        val request = RequestModel(
            UUID.fromString(valueWebId),
            name,
            null
        )
        return addOppositionToValueWeb(request)
    }

    override fun addOppositionWithCharacter(valueWebId: String, name: NonBlankString, characterId: String): Deferred<OppositionAddedToValueWeb> {
        val request = RequestModel(
            UUID.fromString(valueWebId),
            name,
            CharacterId(UUID.fromString(characterId))
        )
        return addOppositionToValueWeb(request)
    }

    private fun addOppositionToValueWeb(requestModel: RequestModel): Deferred<OppositionAddedToValueWeb>
    {
        val deferred = CompletableDeferred<OppositionAddedToValueWeb>()
        threadTransformer.async {
            addOppositionToValueWeb.invoke(requestModel) {
                deferred.complete(it.oppositionAddedToValueWeb)
                addOppositionToValueWebOutputPort.addedOppositionToValueWeb(it)
            }
        }
        return deferred
    }

}