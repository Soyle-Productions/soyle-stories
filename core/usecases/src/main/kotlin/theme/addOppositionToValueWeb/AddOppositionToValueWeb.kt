package com.soyle.stories.usecase.theme.addOppositionToValueWeb

import com.soyle.stories.domain.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

interface AddOppositionToValueWeb {

    class RequestModel(
        val valueWebId: UUID,
        val name: NonBlankString? = null,
        val firstLinkedItem: CharacterId? = null
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    @Deprecated(
        message = "Old api",
        replaceWith = ReplaceWith(
            "this.invoke(RequestModel(valueWebId), output)",
            "com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb.RequestModel"
        )
    )
    suspend operator fun invoke(valueWebId: UUID, output: OutputPort)

    class ResponseModel(
        val oppositionAddedToValueWeb: OppositionAddedToValueWeb,
        val symbolicRepresentationRemoved: RemovedSymbolicItem?,
        val symbolicRepresentationAddedToOpposition: SymbolicRepresentationAddedToOpposition?,
        val characterIncludedInTheme: CharacterIncludedInTheme?
    )

    fun interface OutputPort {
        suspend fun addedOppositionToValueWeb(response: ResponseModel)
    }

}