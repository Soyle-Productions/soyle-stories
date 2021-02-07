package com.soyle.stories.usecase.theme.createTheme

import com.soyle.stories.domain.theme.Theme
import java.util.*

class CreatedTheme(
    val projectId: UUID,
    val themeId: UUID,
    val themeName: String
) {

    constructor(theme: Theme) : this(theme.projectId.uuid, theme.id.uuid, theme.name)

}