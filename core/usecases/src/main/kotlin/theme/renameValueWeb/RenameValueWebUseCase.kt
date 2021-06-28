package com.soyle.stories.usecase.theme.renameValueWeb

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.ValueWebAlreadyHasName
import com.soyle.stories.usecase.theme.ValueWebDoesNotExist
import java.util.*

class RenameValueWebUseCase(
    private val themeRepository: ThemeRepository
) : RenameValueWeb {

    override suspend fun invoke(valueWebId: UUID, name: NonBlankString, output: RenameValueWeb.OutputPort) {
        val (theme, valueWeb) = getThemeAndValueWeb(valueWebId)
        preventInvalidOrDuplicateName(name, valueWeb)
        renameValueWeb(theme, valueWeb, name)
        val response = RenamedValueWeb(theme.id.uuid, valueWeb.id.uuid, valueWeb.name.value, name.value)
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
        name: NonBlankString,
        valueWeb: ValueWeb
    ) {
        if (valueWeb.name == name) throw ValueWebAlreadyHasName(valueWeb.id.uuid, name.value)
    }

    private suspend fun renameValueWeb(
        theme: Theme,
        valueWeb: ValueWeb,
        name: NonBlankString
    ) {
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(valueWeb.withName(name))
        themeRepository.updateTheme(updatedTheme)
    }

}