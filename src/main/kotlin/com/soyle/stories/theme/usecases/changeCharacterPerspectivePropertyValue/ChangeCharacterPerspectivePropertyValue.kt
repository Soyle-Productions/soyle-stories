package com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue

import com.soyle.stories.theme.ThemeException
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
        fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException)
        suspend fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ResponseModel)
    }
}