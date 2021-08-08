package com.soyle.stories.domain.location.exceptions

import com.soyle.stories.domain.location.Location

sealed interface LocationException {
    val locationId: Location.Id
}