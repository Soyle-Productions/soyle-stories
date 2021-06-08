package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.HostedSceneItem
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel
import tornadofx.observableListOf
import tornadofx.toObservable

class LocationDetailsModel : ViewModel(), LocationDetailsViewModel {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope

	override fun update(op: GUIUpdate.() -> Unit) {
		val guiUpdate = object : GUIUpdate {}
		threadTransformer.gui {
			guiUpdate.op()
		}
	}

	val toolNameProperty = SimpleStringProperty("")
	override var GUIUpdate.toolName: String
		get() = toolNameProperty.get()
		set(value) = toolNameProperty.set(value)
	//
	val descriptionProperty = SimpleStringProperty("")
	override var GUIUpdate.description: String
		get() = descriptionProperty.get()
		set(value) = descriptionProperty.set(value)
	//
	val descriptionLabelProperty = SimpleStringProperty("")
	override var GUIUpdate.descriptionLabel: String
		get() = descriptionLabelProperty.get()
		set(value) = descriptionLabelProperty.set(value)
	//
	val availableScenesToHostProperty = SimpleListProperty<AvailableSceneToHostViewModel>(null)
	override var GUIUpdate.availableScenesToHost: List<AvailableSceneToHostViewModel>?
		get() = availableScenesToHostProperty.get()
		set(value) = availableScenesToHostProperty.set(value?.toObservable())
	//
	val hostedScenesProperty = SimpleListProperty<HostedSceneItemModel>(observableListOf())
	override var GUIUpdate.hostedScenes: List<HostedSceneItemViewModel>
		get() = hostedScenesProperty.value
		set(value) {
			hostedScenesProperty.value.setAll(value.filterIsInstance<HostedSceneItemModel>())
		}

	override fun hostedSceneItemViewModel(sceneId: Scene.Id, name: String): HostedSceneItemViewModel =
		HostedSceneItemModel(sceneId, name)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.projectScope.applicationScope)

	class HostedSceneItemModel(
		override val sceneId: Scene.Id,
		name: String
		) : HostedSceneItemViewModel {

		val nameProperty = SimpleStringProperty(name)
		override var name: String
			get() = nameProperty.get()
			set(value) { nameProperty.set(value) }
	}
}
