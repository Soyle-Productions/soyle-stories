package com.soyle.stories.usecase.theme.renameSymbolicItems

import java.util.*

class RenamedSymbolicItem(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionId: UUID,
    val symbolicItemId: UUID,
    val newName: String
)