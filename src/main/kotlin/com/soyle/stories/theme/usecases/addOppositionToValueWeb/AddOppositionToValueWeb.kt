package com.soyle.stories.theme.usecases.addOppositionToValueWeb

import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.entities.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

interface AddOppositionToValueWeb {

    class RequestModel(
        val valueWebId: UUID,
        val name: String? = null,
        val firstLinkedItem: CharacterId? = null
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    @Deprecated(
        message = "Old api",
        replaceWith = ReplaceWith(
            "this.invoke(RequestModel(valueWebId), output)",
            "com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel"
        )
    )
    suspend operator fun invoke(valueWebId: UUID, output: OutputPort)

    class ResponseModel(
        val oppositionAddedToValueWeb: OppositionAddedToValueWeb,
        val symbolicRepresentationRemoved: RemovedSymbolicItem?,
        val symbolicRepresentationAddedToOpposition: SymbolicRepresentationAddedToOpposition?,
        val characterIncludedInTheme: CharacterIncludedInTheme?
    )

    interface OutputPort {
        suspend fun addedOppositionToValueWeb(response: ResponseModel)
    }

}