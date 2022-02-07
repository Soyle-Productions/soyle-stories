package com.soyle.stories.scene.characters.tool

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.layouts.LayoutStyles
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.scene.SceneStyles
import com.soyle.stories.scene.characters.tool.SceneCharactersInviteImageView.Companion.sceneCharactersInviteImage
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import com.soyle.stories.scene.characters.inspect.characterInSceneInspection
import com.soyle.stories.scene.characters.list.*
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItem
import javafx.animation.Animation
import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import tornadofx.*


fun SceneCharactersTool(viewModel: SceneCharactersToolViewModel, locale: SceneCharactersToolLocale): Parent {
    return VBox().apply { addClass(SceneCharactersToolStyles.sceneCharacters); elevation = Elevation.getValue(8) }.apply {
        hbox { addClass(SceneStyles.selectedSceneHeader) }.apply {
            toolTitle {
                scopedListener(viewModel.sceneSelection) {
                    when (it) {
                        is SceneCharactersToolViewModel.SceneSelection.Selected -> textProperty().bind(locale.selectedScene(it.sceneName))
                        else -> textProperty().bind(locale.noSceneSelected)
                    }
                }
            }
        }
        stackpane { vgrow = Priority.ALWAYS; viewOrder = 1.0 }.apply {
            sceneCharactersBody(
                sceneSelection = viewModel.sceneSelection,
                onOpenSceneList = viewModel.onOpenSceneList,
                locale = locale
            )
            animatedCharacterEditor(
                characterFocus = viewModel.focusedCharacter,
                onCloseEditor = viewModel.onCloseEditor
            )
        }

    }
}

private fun Parent.sceneCharactersBody(
    sceneSelection: ObservableValue<SceneCharactersToolViewModel.SceneSelection>,
    onOpenSceneList: () -> Unit,
    locale: SceneCharactersToolLocale
) {
    stackpane {
        elevation = Elevation.getValue(8)
        dynamicContent(sceneSelection) {
            when (it) {
                SceneCharactersToolViewModel.SceneSelection.None, null -> emptyToolInvitation {
                    sceneCharactersInviteImage()
                    toolTitle(locale.sceneCharactersTitle)
                    noSceneSelectedMessage(locale, onOpenSceneList)
                }
                is SceneCharactersToolViewModel.SceneSelection.Loading -> {
                    progressindicator()
                }
                is SceneCharactersToolViewModel.SceneSelection.Loaded -> {
                    charactersInScene(
                        locale.list,
                        it.viewModel
                    )
                }
            }
        }
    }
}

private fun Parent.noSceneSelectedMessage(
    locale: SceneCharactersToolLocale,
    onOpenSceneList: () -> Unit
) {
    markdown(locale.noSceneSelectedInviteMessage).apply {
        addClass(LayoutStyles.inviteMessage)
    }
}

private fun Parent.animatedCharacterEditor(
    characterFocus: ObservableValue<CharacterInSceneInspectionViewModel?>,
    onCloseEditor: () -> Unit
) {
    var currentAnimation: Animation? = null
    val animDuration = Duration.millis(300.0)

    stackpane {
        val clipRect = Rectangle().apply {
            heightProperty().bind(this@stackpane.heightProperty())
            widthProperty().bind(this@stackpane.widthProperty())
        }
        clip = clipRect
        elevation = Elevation.getValue(16)


        scopedListener(characterFocus) {
            currentAnimation?.let { it.stop() }
            when (it) {
                null -> {
                    currentAnimation = fade(animDuration, 0.0) {
                        setOnFinished {
                            if (currentAnimation == this) currentAnimation = null
                            children.clear()
                            exists = false
                        }
                    }
                }
                else -> {
                    minHeight = 0.0
                    maxHeight = 0.0
                    opacity = 1.0
                    exists = true

                    children.clear()
                    characterInSceneInspection(
                        it,
                        header = CharacterInSceneItem(it.itemViewModel) {
                            button("DONE") {
                                addClass(ComponentsStyles.primary, ComponentsStyles.filled)
                                action { onCloseEditor() }
                            }
                        }
                    )
                    println("added inspection")
                    maxHeightProperty().animate(
                        parent?.layoutBounds?.height ?: 0.0,
                        animDuration,
                        Interpolator.EASE_IN
                    ) {
                        currentAnimation = this
                        setOnFinished {
                            minHeight = Region.USE_COMPUTED_SIZE
                            maxHeight = Region.USE_COMPUTED_SIZE
                            if (currentAnimation == this) currentAnimation = null
                        }
                    }
                }
            }
        }
    }
}

class SceneCharactersToolStyles : Stylesheet() {
    companion object {
        val sceneCharacters by cssclass()

        init {
            importStylesheet<SceneCharactersToolStyles>()
        }
    }

    init {
    }
}