package com.soyle.stories.layout.usecases.removeToolsWithId

import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import java.util.*

interface RemoveToolsWithId {

	suspend operator fun invoke(id: UUID, output: OutputPort)

	interface OutputPort {
		fun failedToRemoveToolsWithId(failure: Exception)
		fun toolsRemovedWithId(response: GetSavedLayout.ResponseModel)
	}

}