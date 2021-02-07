package com.soyle.stories.usecase.theme.removeSymbolicItem

import java.util.*

class RemovedSymbolicItem(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val symbolicItemId: UUID
)