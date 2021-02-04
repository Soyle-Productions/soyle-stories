package com.soyle.stories.theme.usecases.renameOppositionValue

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.entities.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.theme.OppositionValueAlreadyHasName
import com.soyle.stories.theme.OppositionValueDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.validateOppositionValueName
import java.util.*

class RenameOppositionValueUseCase(
    private val themeRepository: ThemeRepository
) : RenameOppositionValue {

    override suspend fun invoke(oppositionValueId: UUID, name: String, output: RenameOppositionValue.OutputPort) {
        val theme = getThemeWIthOppositionValue(oppositionValueId)
        validateOppositionValueName(name)
        val (valueWeb, oppositionValue) = getValueWebAndOppositionValue(theme, oppositionValueId)
        if (oppositionValue.name == name) throw OppositionValueAlreadyHasName(oppositionValue.id.uuid, name)
        updateOppositionValueName(oppositionValue, name, valueWeb, theme)
        output.oppositionValueRenamed(
            RenamedOppositionValue(
                theme.id.uuid,
                valueWeb.id.uuid,
                oppositionValue.id.uuid,
                name
            )
        )

    }

    private suspend fun getThemeWIthOppositionValue(oppositionValueId: UUID) =
        (themeRepository.getThemeContainingOppositionValueWithId(OppositionValue.Id(oppositionValueId))
            ?: throw OppositionValueDoesNotExist(oppositionValueId))

    private fun getValueWebAndOppositionValue(
        theme: Theme,
        oppositionValueId: UUID
    ): Pair<ValueWeb, OppositionValue> {
        return theme.valueWebs.asSequence().flatMap { valueWeb ->
            valueWeb.oppositions.asSequence().map {
                valueWeb to it
            }
        }.find { it.second.id.uuid == oppositionValueId }!!
    }

    private suspend fun updateOppositionValueName(
        oppositionValue: OppositionValue,
        name: String,
        valueWeb: ValueWeb,
        theme: Theme
    ) {
        val updatedValueWeb = valueWeb.withOppositionRenamedTo(oppositionValue.id, name)
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(updatedValueWeb)
        themeRepository.updateTheme(updatedTheme)
    }
}