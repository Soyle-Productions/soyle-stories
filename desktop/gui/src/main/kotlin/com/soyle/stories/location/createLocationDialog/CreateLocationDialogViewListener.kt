package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.SingleNonBlankLine

interface CreateLocationDialogViewListener {

	fun getValidState()
	fun createLocation(name: SingleNonBlankLine, description: String)

}