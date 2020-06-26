package com.soyle.stories.theme.usecases.addValueWebToTheme

import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import java.util.*

class ValueWebAddedToTheme(
    val themeId: UUID,
    val valueWebId: UUID,
    val valueWebName: String,
    val oppositionAddedToValueWeb: OppositionAddedToValueWeb
)
