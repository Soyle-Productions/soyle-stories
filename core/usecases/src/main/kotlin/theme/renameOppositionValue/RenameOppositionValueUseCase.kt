package com.soyle.stories.usecase.theme.renameOppositionValue

import com.soyle.stories.domain.theme.OppositionValueDoesNotExist
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.OppositionValueAlreadyHasName
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RenameOppositionValueUseCase(
    private val themeRepository: ThemeRepository
) : RenameOppositionValue {

    override suspend fun invoke(oppositionValueId: UUID, name: NonBlankString, output: RenameOppositionValue.OutputPort) {
        val theme = getThemeWIthOppositionValue(oppositionValueId)
        val (valueWeb, oppositionValue) = getValueWebAndOppositionValue(theme, oppositionValueId)
        if (oppositionValue.name == name) throw OppositionValueAlreadyHasName(oppositionValue.id.uuid, name.value)
        updateOppositionValueName(oppositionValue, name, valueWeb, theme)
        output.oppositionValueRenamed(
            RenamedOppositionValue(
                theme.id.uuid,
                valueWeb.id.uuid,
                oppositionValue.id.uuid,
                name.value
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
        name: NonBlankString,
        valueWeb: ValueWeb,
        theme: Theme
    ) {
        val updatedValueWeb = valueWeb.withOppositionRenamedTo(oppositionValue.id, name)
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(updatedValueWeb)
        themeRepository.updateTheme(updatedTheme)
    }
}