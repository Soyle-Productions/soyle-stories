package com.soyle.stories.storyevent.details

import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventDetails

fun interface StoryEventDetailsPrompt {

    suspend fun showDetails(details: StoryEventDetails)

}