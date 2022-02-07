package com.soyle.stories.scene

import com.soyle.stories.domain.scene.Scene
import javafx.beans.value.ObservableValue

interface FocusedSceneQueries {

    val focusedScene: Scene.Id?
    fun focusedScene(): ObservableValue<Scene.Id?>

}