package com.soyle.stories.domain.location.events

import com.soyle.stories.domain.location.Location

class LocationRenamed(val locationId: Location.Id, val newName: String)