package com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue

import java.util.*

interface ChangeCharacterPerspectivePropertyValue {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val targetCharacterId: UUID,
        val property: Property,
        val value: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(val themeId: UUID,
                        val perspectiveCharacterId: UUID,
                        val targetCharacterId: UUID, val property: Property, val newValue: String)

    enum class Property {
        Attack,
        Similarities
    }

    interface OutputPort {
        fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: Exception)
        suspend fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ResponseModel)
    }
}