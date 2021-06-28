package com.soyle.stories.scene

import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class SceneStyles : Stylesheet() {
    companion object {
        val selectedSceneHeader by cssclass()
        init {
            importStylesheet<SceneStyles>()
        }
    }

    init {
        selectedSceneHeader {
            padding = box(16.px)
            backgroundColor = multi(Color.WHITE)
            effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.25), 4.0, 0.0, 0.0, 4.0)
        }
    }
}