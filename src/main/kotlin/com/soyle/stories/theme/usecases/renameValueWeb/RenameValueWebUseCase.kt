package com.soyle.stories.theme.usecases.renameValueWeb

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.ValueWebAlreadyHasName
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.entities.theme.valueWeb.validateValueWebName
import java.util.*

class RenameValueWebUseCase(
    private val themeRepository: ThemeRepository
) : RenameValueWeb {

    override suspend fun invoke(valueWebId: UUID, name: String, output: RenameValueWeb.OutputPort) {
        val (theme, valueWeb) = getThemeAndValueWeb(valueWebId)
        preventInvalidOrDuplicateName(name, valueWeb)
        renameValueWeb(theme, valueWeb, name)
        val response = RenamedValueWeb(theme.id.uuid, valueWeb.id.uuid, valueWeb.name, name)
        output.valueWebRenamed(response)
    }

    private suspend fun getThemeAndValueWeb(valueWebId: UUID): Pair<Theme, ValueWeb> {
        val theme = getTheme(valueWebId)
        val valueWeb = theme.valueWebs.find { it.id.uuid == valueWebId }!!
        return Pair(theme, valueWeb)
    }

    private suspend fun getTheme(valueWebId: UUID) =
        (themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId))

    private fun preventInvalidOrDuplicateName(
        name: String,
        valueWeb: ValueWeb
    ) {
        validateValueWebName(name)
        if (valueWeb.name == name) throw ValueWebAlreadyHasName(valueWeb.id.uuid, name)
    }

    private suspend fun renameValueWeb(
        theme: Theme,
        valueWeb: ValueWeb,
        name: String
    ) {
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(valueWeb.withName(name))
        themeRepository.updateTheme(updatedTheme)
    }

}