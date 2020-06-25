package com.soyle.stories.theme.usecases.addValueWebToTheme

import java.util.*

class ValueWebAddedToTheme(
    val themeId: UUID,
    val valueWebId: UUID,
    val valueWebName: String,
    val oppositionAddedToValueWeb: OppositionAddedToValueWeb
)

class OppositionAddedToValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val oppositionValueName: String
)