package com.soyle.stories.character.delete.ramifications

import com.soyle.stories.character.removeCharacterFromStory.RamificationsReport
import com.soyle.stories.usecase.character.remove.PotentialChangesOfRemovingCharacterFromStory
import javafx.beans.property.ReadOnlyBooleanProperty
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.objectBinding
import tornadofx.objectProperty

class RemoveCharacterRamificationsReportViewModel : RamificationsReport {

    private val _item = objectProperty<PotentialChangesOfRemovingCharacterFromStory>()

    private val _items = _item.objectBinding { it?.toList() }
    fun items() = _items


    private val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

    private var onCancel: () -> Unit = {}
    fun cancel() {
        onCancel()
    }

    override suspend fun showRamifications(potentialChanges: PotentialChangesOfRemovingCharacterFromStory): Unit? {
        val deferred = CompletableDeferred<Unit?>()
        println("potential changes: ${potentialChanges.toList()}")
        _item.set(potentialChanges)
        _isNeeded.set(true)

        onCancel = { deferred.complete(null) }

        return deferred.await().also { _isNeeded.set(false) }
    }

}