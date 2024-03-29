package com.soyle.stories.usecase.theme.listValueWebsInTheme

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ListValueWebsInThemeUseCase(
    private val themeRepository: ThemeRepository
) : ListValueWebsInTheme {

    override suspend fun invoke(themeId: UUID, output: ListValueWebsInTheme.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        output.valueWebsListedInTheme(ValueWebList(theme.valueWebs.map { ValueWebItem(it.id.uuid, it.name.value) }))
    }
}