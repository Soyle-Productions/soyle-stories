package com.soyle.stories.location.usecases

import com.soyle.stories.location.LocationNameCannotBeBlank

fun validateLocationName(name: String)
{
	if (name.isBlank()) throw LocationNameCannotBeBlank()
}