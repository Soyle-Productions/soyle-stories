package com.soyle.stories.domain.theme.oppositionValue

import com.soyle.stories.domain.validation.ValidationException
import java.util.*

class OppositionValueAlreadyHasEntity(val entityId: UUID, val entityName: String, val oppositionValueId: UUID) : ValidationException()