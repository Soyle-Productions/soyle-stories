package com.soyle.stories.entities.theme.valueWeb

import com.soyle.stories.common.DuplicateOperationException
import java.util.*

class OppositionValueAlreadyInValueWeb(
    val valueWebId: UUID,
    val oppositionValueId: UUID
) : DuplicateOperationException()