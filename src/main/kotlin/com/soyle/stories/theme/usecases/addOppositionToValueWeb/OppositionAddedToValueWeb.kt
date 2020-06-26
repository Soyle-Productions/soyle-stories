package com.soyle.stories.theme.usecases.addOppositionToValueWeb

import java.util.*

class OppositionAddedToValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val oppositionValueName: String
)