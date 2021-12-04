package com.soyle.stories.scene.outline

import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene

interface SceneOutlineReport : ListStoryEventsCoveredByScene.OutputPort {

    suspend fun displayFailure(failure: Throwable)

}