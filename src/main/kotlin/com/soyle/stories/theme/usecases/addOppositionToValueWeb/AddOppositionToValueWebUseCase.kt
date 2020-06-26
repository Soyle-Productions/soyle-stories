package com.soyle.stories.theme.usecases.addOppositionToValueWeb

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class AddOppositionToValueWebUseCase(
    private val themeRepository: ThemeRepository
) : AddOppositionToValueWeb {

    override suspend fun invoke(valueWebId: UUID, output: AddOppositionToValueWeb.OutputPort) {
        val (theme, valueWeb) = getThemeAndValueWeb(valueWebId)
        val oppositionValue = createOppositionValue(valueWeb, theme)
        val response = OppositionAddedToValueWeb(theme.id.uuid, valueWebId, oppositionValue.id.uuid, oppositionValue.name)
        output.addedOppositionToValueWeb(response)
    }

    private suspend fun createOppositionValue(
        valueWeb: ValueWeb,
        theme: Theme
    ): OppositionValue {
        val oppositionValue = OppositionValue("${valueWeb.name} ${valueWeb.oppositions.size+1}")
        val update = theme.withoutValueWeb(valueWeb.id)
            .withValueWeb(
                valueWeb.withOpposition(oppositionValue)
            )
        themeRepository.updateTheme(update)
        return oppositionValue
    }

    private suspend fun getThemeAndValueWeb(valueWebId: UUID): Pair<Theme, ValueWeb> {
        val theme = themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId)

        return theme to theme.valueWebs.find { it.id.uuid == valueWebId }!!
    }
}