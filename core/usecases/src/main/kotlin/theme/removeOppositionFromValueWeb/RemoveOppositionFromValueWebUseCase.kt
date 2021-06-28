package com.soyle.stories.usecase.theme.removeOppositionFromValueWeb

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.theme.valueWeb.ValueWebDoesNotContainOppositionValue
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.ValueWebDoesNotExist
import java.util.*

class RemoveOppositionFromValueWebUseCase(
    private val themeRepository: ThemeRepository
) : RemoveOppositionFromValueWeb {

    override suspend fun invoke(oppositionId: UUID, valueWebId: UUID, output: RemoveOppositionFromValueWeb.OutputPort) {
        val (theme, valueWeb, oppositionValue) = getThemeEntities(valueWebId, oppositionId)
        removeOppositionValue(theme, valueWeb, oppositionValue)
        val response = OppositionRemovedFromValueWeb(theme.id.uuid, valueWebId, oppositionId)
        output.removedOppositionFromValueWeb(response)
    }

    private suspend fun removeOppositionValue(
        theme: Theme,
        valueWeb: ValueWeb,
        oppositionValue: OppositionValue
    ) {
        val updatedValueWeb = valueWeb.withoutOpposition(oppositionValue.id)
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(updatedValueWeb)
        themeRepository.updateTheme(updatedTheme)
    }

    private suspend fun getThemeEntities(
        valueWebId: UUID,
        oppositionId: UUID
    ): Triple<Theme, ValueWeb, OppositionValue> {
        val theme = getTheme(valueWebId)
        val valueWeb = theme.valueWebs.find { it.id.uuid == valueWebId }!!
        val oppositionValue = getOppositionValue(valueWeb, oppositionId)
        return Triple(theme, valueWeb, oppositionValue)
    }

    private suspend fun getTheme(valueWebId: UUID) =
        (themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId))

    private fun getOppositionValue(
        valueWeb: ValueWeb,
        oppositionId: UUID
    ) = (valueWeb.oppositions.find { it.id.uuid == oppositionId }
        ?: throw ValueWebDoesNotContainOppositionValue(
            valueWeb.id.uuid,
            oppositionId
        ))
}