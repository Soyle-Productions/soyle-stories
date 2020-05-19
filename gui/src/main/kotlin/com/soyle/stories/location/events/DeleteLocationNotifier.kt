package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation

class DeleteLocationNotifier : DeleteLocation.OutputPort, Notifier<DeleteLocation.OutputPort>() {
	override fun receiveDeleteLocationFailure(failure: LocationException) {
		notifyAll { it.receiveDeleteLocationFailure(failure) }
	}

	override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
		notifyAll { it.receiveDeleteLocationResponse(response) }
	}
}