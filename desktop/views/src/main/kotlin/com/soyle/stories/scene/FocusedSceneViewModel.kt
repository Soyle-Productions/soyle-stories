package com.soyle.stories.scene

import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import javafx.beans.value.ObservableValue
import tornadofx.Scope
import tornadofx.objectProperty
import tornadofx.getValue

class FocusedSceneViewModel(scope: Scope) : FocusedSceneQueries {

    private val _focusedScene = objectProperty<Scene.Id?>(null)

    override val focusedScene: Scene.Id? by _focusedScene

    override fun focusedScene(): ObservableValue<Scene.Id?> = _focusedScene

    fun focusOn(scene: Scene.Id) {
        _focusedScene.set(scene)
    }

    private val sceneDeletedReceiver = SceneDeletedReceiver {
        if (it.sceneId == _focusedScene.get()) _focusedScene.set(null)
    } listensTo scope.get<SceneDeletedNotifier>()

}