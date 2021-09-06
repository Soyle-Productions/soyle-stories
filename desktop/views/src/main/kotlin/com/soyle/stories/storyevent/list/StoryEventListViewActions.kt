package com.soyle.stories.storyevent.list

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.toObservable

interface StoryEventListViewActions {

    fun loadStoryEvents()
    fun createStoryEvent()
    fun insertStoryEventBeforeSelectedItem()
    fun insertStoryEventAtSameTimeAsSelectedItem()
    fun insertStoryEventAfterSelectedItem()
    fun renameSelectedItem()
    fun rescheduleSelectedItem()
    fun adjustTimesOfSelectedItems()
    fun deleteSelectedItems()
}