package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation

class DeleteLocationNotifier(
	private val threadTransformer: ThreadTransformer
) : DeleteLocation.OutputPort, Notifier<DeleteLocation.OutputPort>() {
	override fun receiveDeleteLocationFailure(failure: LocationException) {
		threadTransformer.async {
			notifyAll { it.receiveDeleteLocationFailure(failure) }
		}
	}

	override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveDeleteLocationResponse(response) }
		}
	}
}