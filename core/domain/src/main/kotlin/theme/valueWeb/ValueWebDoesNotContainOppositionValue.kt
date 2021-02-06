package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*

class ValueWebDoesNotContainOppositionValue(val valueWebId: UUID, val oppositionValueId: UUID) : EntityNotFoundException(oppositionValueId)