package com.soyle.stories.usecase.theme.addOppositionToValueWeb

import java.util.*

open class OppositionAddedToValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val oppositionValueName: String,
    val needsName: Boolean
)