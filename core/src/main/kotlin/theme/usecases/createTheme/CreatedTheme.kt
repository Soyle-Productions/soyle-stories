package com.soyle.stories.theme.usecases.createTheme

import com.soyle.stories.entities.Theme
import java.util.*

class CreatedTheme(
    val projectId: UUID,
    val themeId: UUID,
    val themeName: String
) {

    constructor(theme: Theme) : this(theme.projectId.uuid, theme.id.uuid, theme.name)

}