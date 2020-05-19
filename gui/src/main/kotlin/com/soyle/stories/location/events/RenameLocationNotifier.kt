package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

class RenameLocationNotifier : RenameLocation.OutputPort, Notifier<RenameLocation.OutputPort>() {
	override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
		notifyAll { it.receiveRenameLocationResponse(response) }
	}

	override fun receiveRenameLocationFailure(failure: LocationException) {
		notifyAll { it.receiveRenameLocationFailure(failure) }
	}
}