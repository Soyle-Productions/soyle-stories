package com.soyle.stories.theme.usecases.addValueWebToTheme

import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicItemId
import com.soyle.stories.entities.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface AddValueWebToTheme {

    class RequestModel(val themeId: UUID, val name: String, val automaticallyLinkItem: SymbolicItemId? = null)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    @Deprecated(message = "Outdated api", replaceWith = ReplaceWith("this.invoke(RequestModel(themeId, name), output)"), level = DeprecationLevel.ERROR)
    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    class ResponseModel(
        val addedValueWeb: ValueWebAddedToTheme,
        val includedCharacter: CharacterIncludedInTheme?,
        val symbolicItemAdded: SymbolicRepresentationAddedToOpposition?
    )

    interface OutputPort {
        suspend fun addedValueWebToTheme(response: ResponseModel)
    }
}