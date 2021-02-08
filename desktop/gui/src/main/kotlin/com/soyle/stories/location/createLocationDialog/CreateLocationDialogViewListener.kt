package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.domain.validation.SingleNonBlankLine

interface CreateLocationDialogViewListener {

	fun getValidState()
	fun createLocation(name: SingleNonBlankLine, description: String)

}