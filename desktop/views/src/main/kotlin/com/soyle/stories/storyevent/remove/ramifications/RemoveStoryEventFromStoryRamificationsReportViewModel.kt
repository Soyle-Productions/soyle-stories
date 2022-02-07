package com.soyle.stories.storyevent.remove.ramifications

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.remove.RemoveStoryEventFromProjectRamificationsReport
import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import com.soyle.stories.usecase.storyevent.remove.PotentialChangesOfRemovingStoryEventFromProject
import javafx.beans.property.ReadOnlyBooleanProperty
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import tornadofx.booleanProperty
import tornadofx.objectBinding
import tornadofx.objectProperty

class RemoveStoryEventFromStoryRamificationsReportViewModel : RemoveStoryEventFromProjectRamificationsReport {

    private val _item = objectProperty<PotentialChangesOfRemovingStoryEventFromProject>()

    private val _items = _item.objectBinding { it?.toList() }
    fun items() = _items


    private val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

    private var onCancel: () -> Unit = {}
    fun cancel() {
        onCancel()
    }

    override suspend fun showPotentialChanges(potentialChanges: PotentialChangesOfRemovingStoryEventFromProject) {
        val deferred = Job()
        _item.set(potentialChanges)
        _isNeeded.set(true)

        onCancel = { deferred.cancel() }

        return deferred.join()
    }
}