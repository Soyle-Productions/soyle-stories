package com.soyle.stories.usecase.theme.addValueWebToTheme

import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import java.util.*

class ValueWebAddedToTheme(
    val themeId: UUID,
    val valueWebId: UUID,
    val valueWebName: String,
    val oppositionAddedToValueWeb: OppositionAddedToValueWeb
)
