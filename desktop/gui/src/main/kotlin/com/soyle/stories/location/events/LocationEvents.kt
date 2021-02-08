package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation
import com.soyle.stories.usecase.location.renameLocation.RenameLocation

interface LocationEvents {

	val createNewLocation: Notifier<CreateNewLocation.OutputPort>
	val deleteLocation: Notifier<DeletedLocationReceiver>
	val renameLocation: Notifier<RenameLocation.OutputPort>
	val reDescribeLocation: Notifier<ReDescribeLocation.OutputPort>

}