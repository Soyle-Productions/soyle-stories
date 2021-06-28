package com.soyle.stories.location.redescribeLocation

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation

class ReDescribeLocationNotifier(
	private val threadTransformer: ThreadTransformer
) : ReDescribeLocation.OutputPort, Notifier<ReDescribeLocation.OutputPort>() {
	override fun receiveReDescribeLocationFailure(failure: Exception) {
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