package com.soyle.stories.theme.usecases.removeValueWebFromTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class RemoveValueWebFromThemeUseCase(
    private val themeRepository: ThemeRepository
) : RemoveValueWebFromTheme {

    override suspend fun invoke(valueWebId: UUID, output: RemoveValueWebFromTheme.OutputPort) {
        val theme = getTheme(valueWebId)
        removeValueWeb(theme, valueWebId)
        val response = ValueWebRemovedFromTheme(theme.id.uuid, valueWebId)
        output.removedValueWebFromTheme(response)
    }

    private suspend fun getTheme(valueWebId: UUID) =
        (themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId))

    private suspend fun removeValueWeb(theme: Theme, valueWebId: UUID) {
        themeRepository.updateTheme(theme.withoutValueWeb(ValueWeb.Id(valueWebId)))
    }
}