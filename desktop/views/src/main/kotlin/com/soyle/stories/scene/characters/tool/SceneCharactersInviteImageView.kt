package com.soyle.stories.scene.characters.tool

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.deepRequestLayout
import javafx.event.EventTarget
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*

class SceneCharactersInviteImageView : ImageView(Image("com/soyle/stories/scene/Symbols-design.png", true)) {

    companion object {
        @ViewBuilder
        fun EventTarget.sceneCharactersInviteImage(config: SceneCharactersInviteImageView.() -> Unit = {}): SceneCharactersInviteImageView
        {
            val view = SceneCharactersInviteImageView()
            addChildIfPossible(view)
            return view.apply(config)
        }
    }

    init {
        addClass(Styles.sceneCharactersInviteImage)
        isPreserveRatio = true
        fitHeight = 260.0

        placeImage()
    }

    private fun placeImage() {
        if (image.progress == 1.0) {
            if (parent != null) parent.deepRequestLayout()
            else parentProperty().onChangeOnce { parent?.deepRequestLayout() }
        } else {
            image.progressProperty().onChangeOnce {
                placeImage()
            }
        }
    }

    class Styles : Stylesheet() {
        companion object {
            val sceneCharactersInviteImage by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sceneCharactersInviteImage {
                smooth = true
            }
        }
    }
}