package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

class RenameLocationNotifier(
	private val threadTransformer: ThreadTransformer
) : RenameLocation.OutputPort, Notifier<RenameLocation.OutputPort>() {
	override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveRenameLocationResponse(response) }
		}
	}

	override fun receiveRenameLocationFailure(failure: LocationException) {
		threadTransformer.async {
			notifyAll { it.receiveRenameLocationFailure(failure) }
		}
	}
}