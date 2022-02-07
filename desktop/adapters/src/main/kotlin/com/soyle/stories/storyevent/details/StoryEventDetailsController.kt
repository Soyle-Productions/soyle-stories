package com.soyle.stories.storyevent.details

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface StoryEventDetailsController {

    fun getStoryEventDetails(storyEventId: StoryEvent.Id, prompt: StoryEventDetailsPrompt)

    class Implementation(
        mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val getStoryEventDetails: GetStoryEventDetails
    ) : StoryEventDetailsController, CoroutineScope by CoroutineScope(mainContext) {

        override fun getStoryEventDetails(storyEventId: StoryEvent.Id, prompt: StoryEventDetailsPrompt) {
            launch {
                val mainContext = coroutineContext
                withContext(asyncContext) {
                    getStoryEventDetails.invoke(storyEventId) { it: StoryEventDetails ->
                        withContext(mainContext) {
                            prompt.showDetails(it)
                        }
                    }
                }
            }
        }

    }

}