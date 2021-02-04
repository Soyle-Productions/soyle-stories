package com.soyle.stories.theme.usecases.changeCharacterPropertyValue

import com.soyle.stories.theme.ThemeException
import java.util.*

interface ChangeCharacterPropertyValue {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val property: Property,
        val value: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val property: Property, val newValue: String)

    enum class Property {
        Archetype,
        VariationOnMoral,
        Ability
    }

    interface OutputPort {
        fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException)
        fun receiveChangeCharacterPropertyValueResponse(response: ResponseModel)
    }
}