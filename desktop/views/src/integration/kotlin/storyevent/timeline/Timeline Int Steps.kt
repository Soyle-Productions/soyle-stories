package storyevent.timeline

import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents

suspend fun ListStoryEventsControllerDouble.`given story events have been loaded`(vararg items: StoryEventItem) {
    requestedOutputPort!!.receiveListAllStoryEventsResponse(ListAllStoryEvents.ResponseModel(items.toList()))
}