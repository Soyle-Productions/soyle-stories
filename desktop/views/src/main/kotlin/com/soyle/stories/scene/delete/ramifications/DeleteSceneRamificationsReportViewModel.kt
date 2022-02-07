package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.scene.delete.DeleteSceneRamificationsReport
import com.soyle.stories.scene.effects.CharacterWillGainInheritedMotivationViewModel
import com.soyle.stories.scene.effects.InheritedMotivationWillBeClearedViewModel
import com.soyle.stories.usecase.scene.character.effects.CharacterGainedInheritedMotivationInScene
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.scene.character.effects.InheritedCharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.common.AffectedScene
import com.soyle.stories.usecase.scene.delete.PotentialChangesOfDeletingScene
import com.soyle.stories.usecase.storyevent.remove.PotentialChangesOfRemovingStoryEventFromProject
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CompletableDeferred
import tornadofx.*

class DeleteSceneRamificationsReportViewModel : DeleteSceneRamificationsReport {

	private val _isNeeded = booleanProperty(false)
	fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

	private val _items = observableListOf<Any>()
	val items = listProperty(FXCollections.unmodifiableObservableList(_items))

	private var onDelete: () -> Unit = {}

	fun setOnDelete(handler: () -> Unit) {
		onDelete = handler
	}

	fun delete() {
		deferred?.complete(Unit)
		onDelete()
	}

	private var onCancel: () -> Unit = {}
	fun cancel() {
		deferred?.complete(null)
		onCancel()
	}

	private var deferred: CompletableDeferred<Unit?>? = null

	override suspend fun receivePotentialChangesFromDeletingScene(response: PotentialChangesOfDeletingScene) {
		// response.sceneRemoved -- unneeded
		response.hostedScenesRemoved
		response.storyEventsUncovered
		_items.setAll(*response.inheritedCharacterMotivationChanges.mapNotNull {
			when (it) {
				is InheritedCharacterMotivationInSceneCleared -> InheritedMotivationWillBeClearedViewModel(it)
				is CharacterGainedInheritedMotivationInScene -> CharacterWillGainInheritedMotivationViewModel(it)
				else -> null
			}
		}.toTypedArray())
		deferred = CompletableDeferred()

		_isNeeded.set(true)
	}

	override suspend fun requestContinuation(): Unit? {
		return deferred?.await()
	}

}