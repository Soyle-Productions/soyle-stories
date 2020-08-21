package com.soyle.stories.location.redescribeLocation

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation

class ReDescribeLocationNotifier(
	private val threadTransformer: ThreadTransformer
) : ReDescribeLocation.OutputPort, Notifier<ReDescribeLocation.OutputPort>() {
	override fun receiveReDescribeLocationFailure(failure: LocationException) {
		threadTransformer.async {
			notifyAll { it.receiveReDescribeLocationFailure(failure) }
		}
	}

	override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveReDescribeLocationResponse(response) }
		}
	}
}