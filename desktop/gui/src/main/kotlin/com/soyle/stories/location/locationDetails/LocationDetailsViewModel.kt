package com.soyle.stories.location.locationDetails

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.HostedSceneItem

interface GUIUpdate

interface ViewModel {

  fun update(op: GUIUpdate.() -> Unit)
}

interface LocationDetailsViewModel : ViewModel {

  var GUIUpdate.toolName: String
  var GUIUpdate.descriptionLabel: String
  var GUIUpdate.description: String
  var GUIUpdate.availableScenesToHost: List<AvailableSceneToHostViewModel>?
  var GUIUpdate.hostedScenes: List<HostedSceneItemViewModel>

  fun hostedSceneItemViewModel(sceneId: Scene.Id, name: String): HostedSceneItemViewModel
}

interface HostedSceneItemViewModel {

    val sceneId: Scene.Id
    var name: String
}
