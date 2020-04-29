package com.soyle.stories.location.redescribeLocation

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation

class ReDescribeLocationNotifier : ReDescribeLocation.OutputPort, Notifier<ReDescribeLocation.OutputPort>() {
	override fun receiveReDescribeLocationFailure(failure: LocationException) {
		notifyAll { it.receiveReDescribeLocationFailure(failure) }
	}

	override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
		notifyAll { it.receiveReDescribeLocationResponse(response) }
	}
}