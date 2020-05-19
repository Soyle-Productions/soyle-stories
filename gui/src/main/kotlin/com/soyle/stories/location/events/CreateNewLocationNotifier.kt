package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation

class CreateNewLocationNotifier : CreateNewLocation.OutputPort, Notifier<CreateNewLocation.OutputPort>() {
	override fun receiveCreateNewLocationFailure(failure: LocationException) {
		notifyAll { it.receiveCreateNewLocationFailure(failure) }
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		notifyAll { it.receiveCreateNewLocationResponse(response) }
	}
}