package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation

class CreateNewLocationNotifier(
	private val threadTransformer: ThreadTransformer
) : CreateNewLocation.OutputPort, Notifier<CreateNewLocation.OutputPort>() {
	override fun receiveCreateNewLocationFailure(failure: LocationException) {
		threadTransformer.async {
			notifyAll { it.receiveCreateNewLocationFailure(failure) }
		}
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveCreateNewLocationResponse(response) }
		}
	}
}