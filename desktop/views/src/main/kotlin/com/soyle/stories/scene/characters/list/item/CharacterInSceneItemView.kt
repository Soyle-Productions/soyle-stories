package com.soyle.stories.scene.characters.list.item

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.characterarc.CharacterArcStyles
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.*
import com.soyle.stories.common.components.text.Caption.Companion.caption
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import java.util.logging.Logger

fun CharacterInSceneItem(
    viewModel: CharacterInSceneItemViewModel,
    configure: Node.() -> Unit = Node::applyNothing
): Node {
    return object : HBox(), ViewOf<CharacterInSceneItemViewModel> {
        override val viewModel: CharacterInSceneItemViewModel = viewModel

        val hasRole = viewModel.role().isNotBlank()

        init {
            addClass(CharacterInSceneItemStyles.characterInSceneItem)

            add(characterIcon(viewModel.iconSource()))
            vbox {
                addClass(Stylesheet.labelContainer)
                hgrow = Priority.ALWAYS

                sectionTitle(viewModel.name()) {
                    toggleClass(Stylesheet.warning, viewModel.warning.isNotBlank())
                    scopedListener(viewModel.warning()) {
                        if (it.isNullOrBlank()) tooltip = null
                        else {
                            tooltip {
                                isAutoHide = true
                                textProperty().bind(viewModel.warning())
                            }
                        }
                    }
                }
                caption { textProperty().bind(viewModel.role()); existsWhen(hasRole) }
            }
        }
    }.apply(configure)
}

@ViewBuilder
fun Parent.characterInSceneItem(
    viewModel: CharacterInSceneItemViewModel,
    configure: Node.() -> Unit = Node::applyNothing
): Node {
    return CharacterInSceneItem(viewModel, configure)
        .also { add(it) }
}

class CharacterInSceneItemStyles : Stylesheet() {

    companion object {
        val characterInSceneItem by cssclass()

        init {
            styleImporter<CharacterInSceneItemStyles>()
        }
    }

    init {
        characterInSceneItem {
            spacing = 8.px
            padding = box(8.px)
            alignment = Pos.CENTER_LEFT
            fillHeight = false

            CharacterArcStyles.characterIcon {
                backgroundColor = multi(Color.WHITESMOKE)
                backgroundRadius = multi(box(50.percent))
            }
            labelContainer {
                alignment = Pos.CENTER_LEFT
            }
        }
    }

}