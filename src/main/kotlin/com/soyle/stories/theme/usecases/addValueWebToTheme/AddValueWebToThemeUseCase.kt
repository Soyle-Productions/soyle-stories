package com.soyle.stories.theme.usecases.addValueWebToTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.validateValueWebName
import java.util.*

class AddValueWebToThemeUseCase(
    private val themeRepository: ThemeRepository
) : AddValueWebToTheme {
    override suspend fun invoke(themeId: UUID, name: String, output: AddValueWebToTheme.OutputPort) {
        val theme = getTheme(themeId)
        validateValueWebName(name)
        val valueWeb = createValueWeb(name, theme)
        output.addedValueWebToTheme(responseModel(themeId, valueWeb, name))
    }

    private fun responseModel(
        themeId: UUID,
        valueWeb: ValueWeb,
        name: String
    ): ValueWebAddedToTheme {
        return ValueWebAddedToTheme(
            themeId, valueWeb.id.uuid, name,
            OppositionAddedToValueWeb(themeId, valueWeb.id.uuid, valueWeb.oppositions.first().id.uuid, name)
        )
    }

    private suspend fun createValueWeb(name: String, theme: Theme): ValueWeb {
        val valueWeb = ValueWeb(name)
        themeRepository.updateTheme(theme.withValueWeb(valueWeb))
        return valueWeb
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))
}