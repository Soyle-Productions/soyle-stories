package com.soyle.stories.usecase.theme.addValueWebToTheme

import com.soyle.stories.domain.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.SymbolicItemId
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface AddValueWebToTheme {

    class RequestModel(val themeId: UUID, val name: NonBlankString, val automaticallyLinkItem: SymbolicItemId? = null)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    @Deprecated(message = "Outdated api", replaceWith = ReplaceWith("this.invoke(RequestModel(themeId, name), output)"), level = DeprecationLevel.ERROR)
    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    class ResponseModel(
        val addedValueWeb: ValueWebAddedToTheme,
        val includedCharacter: CharacterIncludedInTheme?,
        val symbolicItemAdded: SymbolicRepresentationAddedToOpposition?
    )

    fun interface OutputPort {
        suspend fun addedValueWebToTheme(response: ResponseModel)
    }
}