package com.soyle.stories.location.events

import com.soyle.stories.common.Notifier
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

interface LocationEvents {

	val createNewLocation: Notifier<CreateNewLocation.OutputPort>
	val deleteLocation: Notifier<DeleteLocation.OutputPort>
	val renameLocation: Notifier<RenameLocation.OutputPort>
	val reDescribeLocation: Notifier<ReDescribeLocation.OutputPort>

}