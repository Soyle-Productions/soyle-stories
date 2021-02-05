package com.soyle.stories.entities.theme.valueWeb

import com.soyle.stories.common.EntityNotFoundException
import java.util.*

class ValueWebDoesNotContainOppositionValue(val valueWebId: UUID, val oppositionValueId: UUID) : EntityNotFoundException(oppositionValueId)