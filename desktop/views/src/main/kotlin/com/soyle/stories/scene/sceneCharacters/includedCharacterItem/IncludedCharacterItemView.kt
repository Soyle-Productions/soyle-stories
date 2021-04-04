package com.soyle.stories.scene.sceneCharacters.includedCharacterItem

import com.soyle.stories.characterarc.CharacterArcStyles
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.characterarc.CharacterArcStyles.Companion.characterIcon
import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.buttons.SecondaryButton
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.text.Caption.Companion.caption
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.existsWhen
import com.soyle.stories.scene.sceneCharacters.IncludedCharacterViewModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class IncludedCharacterItemView(val props: IncludedCharacterItemModel) {

    companion object {
        @ViewBuilder
        fun Parent.includedCharacterItem(
            props: ReadOnlyObjectProperty<IncludedCharacterViewModel?>,
            config: IncludedCharacterItemView.() -> Unit = {}
        ): Node {
            val model = IncludedCharacterItemModel()
            model.itemProperty.bind(props)
            return IncludedCharacterItemView(model).apply(config).root.also(::add)
        }
    }

    private val actionButton = Button()

    val actionText: StringProperty = actionButton.textProperty()
    val action: ObjectProperty<EventHandler<ActionEvent>?> = actionButton.onActionProperty()
    fun actionButton(op: Button.() -> Unit)
    {
        actionButton.op()
    }

    val root: Node = HBox().apply {
        addClass(Styles.includedCharacterItem)
        add(characterIcon(props.iconSource))
        vbox {
            alignment = Pos.CENTER_LEFT
            hgrow = Priority.ALWAYS
            sectionTitle(props.characterName)
            caption(props.characterRole) {
                existsWhen(props.characterRole.isNotEmpty)
            }
        }
        add(actionButton)
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this
    }

    fun addTo(parent: Parent) {
        parent.add(root)
    }

    class Styles : Stylesheet() {
        companion object {
            val includedCharacterItem by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            includedCharacterItem {
                spacing = 8.px
                padding = box(8.px)
                alignment = Pos.CENTER_LEFT

                characterIcon {
                    backgroundColor = multi(Color.WHITESMOKE)
                    backgroundRadius = multi(box(50.percent))
                }
            }
        }

    }

}