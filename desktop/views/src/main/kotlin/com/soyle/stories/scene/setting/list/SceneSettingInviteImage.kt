package com.soyle.stories.scene.setting.list

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.image.ImageView
import tornadofx.imageview
import tornadofx.opcr

class SceneSettingInviteImage private constructor() {

    companion object {
        @ViewBuilder
        fun EventTarget.sceneSettingInviteImage() {
            with(SceneSettingInviteImage()) { render() }
        }
    }

    private fun EventTarget.render(): Node {
        return imageview("com/soyle/stories/scene/Symbols-design.png") {
            isPreserveRatio = true
            isSmooth = true
            fitHeight = 260.0
        }
    }

}