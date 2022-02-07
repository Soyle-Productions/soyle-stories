package com.soyle.stories.storyevent.character.remove.ramifications

import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventRamificationsReport
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent
import javafx.beans.property.ReadOnlyBooleanProperty
import kotlinx.coroutines.Job
import tornadofx.booleanProperty
import tornadofx.objectBinding
import tornadofx.objectProperty

class RemoveCharacterFromStoryEventRamificationsReportViewModel : RemoveCharacterFromStoryEventRamificationsReport {

    private val _item = objectProperty<PotentialChangesOfRemovingCharacterFromStoryEvent>()

    private val _items = _item.objectBinding { it?.toList() }
    fun items() = _items

    private val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

    private var onCancel: () -> Unit = {}
    fun cancel() {
        onCancel()
    }

    override suspend fun showRamifications(potentialChanges: PotentialChangesOfRemovingCharacterFromStoryEvent) {
        val deferred = Job()
        _item.set(potentialChanges)
        _isNeeded.set(true)

        onCancel = { deferred.cancel() }

        return deferred.join()
    }

}