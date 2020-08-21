package com.soyle.stories.entities.theme.oppositionValue

import com.soyle.stories.common.ValidationException
import java.util.*

class OppositionValueAlreadyHasEntity(val entityId: UUID, val entityName: String, val oppositionValueId: UUID) : ValidationException()