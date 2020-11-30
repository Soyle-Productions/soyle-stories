package com.soyle.stories.prose

import com.soyle.stories.common.EntityNotFoundException
import java.util.*

data class ProseDoesNotExist(val proseId: UUID) : EntityNotFoundException(proseId)