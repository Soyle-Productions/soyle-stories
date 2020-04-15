package com.soyle.stories.location

import java.util.*

abstract class LocationException : Exception()

class LocationNameCannotBeBlank : LocationException()
class LocationDoesNotExist(val locationId: UUID) : LocationException()