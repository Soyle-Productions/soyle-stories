package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.validation.EntityNotFoundException


data class ProseDoesNotExist(val proseId: Prose.Id) : EntityNotFoundException(proseId.uuid)