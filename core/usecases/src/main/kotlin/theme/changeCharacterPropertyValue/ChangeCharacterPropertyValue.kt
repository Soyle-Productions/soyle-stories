package com.soyle.stories.usecase.theme.changeCharacterPropertyValue

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
        fun receiveChangeCharacterPropertyValueFailure(failure: Exception)
        fun receiveChangeCharacterPropertyValueResponse(response: ResponseModel)
    }
}