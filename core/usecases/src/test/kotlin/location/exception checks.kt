package com.soyle.stories.usecase.location

import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun locationDoesNotExist(locationId: UUID): (Any?) -> Unit = { actual ->
	actual as LocationDoesNotExist
	assertEquals(locationId, actual.locationId)
}