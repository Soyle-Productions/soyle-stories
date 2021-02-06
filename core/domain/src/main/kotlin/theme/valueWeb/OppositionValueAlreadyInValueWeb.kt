package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.validation.DuplicateOperationException
import java.util.*

class OppositionValueAlreadyInValueWeb(
    val valueWebId: UUID,
    val oppositionValueId: UUID
) : DuplicateOperationException()