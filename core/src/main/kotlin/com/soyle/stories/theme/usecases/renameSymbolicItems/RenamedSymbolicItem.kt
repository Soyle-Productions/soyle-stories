package com.soyle.stories.theme.usecases.renameSymbolicItems

import java.util.*

class RenamedSymbolicItem(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionId: UUID,
    val symbolicItemId: UUID,
    val newName: String
)