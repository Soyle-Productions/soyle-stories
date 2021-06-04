package com.soyle.stories.location.hostedScene.listAvailableScenes

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocation
import kotlinx.coroutines.Job

class ListScenesToHostInLocationController(
    private val threadTransformer: ThreadTransformer,
    private val listScenesToHostInLocation: ListScenesToHostInLocation
) {

    fun listScenesToHostInLocation(locationId: Location.Id, output: ListScenesToHostInLocation.OutputPort): Job {
        return threadTransformer.async {
            listScenesToHostInLocation.invoke(locationId, output)
        }
    }
}