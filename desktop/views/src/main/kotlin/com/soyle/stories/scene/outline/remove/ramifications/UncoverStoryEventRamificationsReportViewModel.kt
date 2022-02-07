package com.soyle.stories.scene.outline.remove.ramifications

import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventRamificationsReport
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.Job
import tornadofx.booleanProperty
import tornadofx.observableListOf

class UncoverStoryEventRamificationsReportViewModel : UncoverStoryEventRamificationsReport {

    private val _items = observableListOf<ImplicitCharacterRemovedFromScene>()
    fun items(): ObservableList<ImplicitCharacterRemovedFromScene> = items as ObservableList<ImplicitCharacterRemovedFromScene>
    val items: List<ImplicitCharacterRemovedFromScene> by lazy { FXCollections.unmodifiableObservableList(_items) }

    private val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

    private var onCancel: () -> Unit = {}
    fun cancel() {
        onCancel()
    }

    override suspend fun showRamifications(potentialChanges: PotentialChangesFromUncoveringStoryEvent) {
        val job = Job()

        _items.setAll(potentialChanges)

        onCancel = { job.cancel() }

        _isNeeded.set(true)

        return job.join().also {
            _isNeeded.set(false)
        }

    }

}