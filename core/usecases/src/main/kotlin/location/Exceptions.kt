package com.soyle.stories.usecase.location

import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*


class LocationDoesNotExist(val locationId: UUID) : EntityNotFoundException(locationId)