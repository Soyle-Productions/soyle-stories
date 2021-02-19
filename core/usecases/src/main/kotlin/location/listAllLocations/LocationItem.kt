package com.soyle.stories.usecase.location.listAllLocations

import com.soyle.stories.domain.location.Location

class LocationItem(
  val id: Location.Id,
  val locationName: String
)