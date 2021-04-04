package com.soyle.stories.common.components.dataDisplay.chip

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.layouts.LayoutStyles.Companion.primary
import com.soyle.stories.soylestories.Styles
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.scene.text.FontWeight
import tornadofx.*

class Chip : ButtonBase() {

    val node: Node = this

    init {
        onActionProperty().onChange {
            this@Chip.isFocusTraversable = it != null
            toggleClass(Styles.clickable, it != null)
        }
    }

    private val colorProperty = SimpleObjectProperty<Color>(Color.Default)
    fun colorProperty(): ObjectProperty<Color> = colorProperty
    var color: Color by colorProperty()

    private val variantProperty = SimpleObjectProperty<Variant>(Variant.Default)
    fun variantProperty(): ObjectProperty<Variant> = variantProperty
    var variant: Variant by variantProperty()

    private val onDeleteProperty = SimpleObjectProperty<EventHandler<ActionEvent>?>(null)
    fun onDeleteProperty(): ObjectProperty<EventHandler<ActionEvent>?> = onDeleteProperty
    var onDelete: EventHandler<ActionEvent>? by onDeleteProperty()
    fun onDelete(handler: EventHandler<ActionEvent>) {
        onDelete = handler
    }

    private val deleteGraphicProperty = object : ObjectPropertyBase<Node?>(
        SVGPath().apply {
            content =
                "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"
        }
    ) {
        override fun invalidated() {
            get()?.styleClass?.add(Styles.chipDeleteIcon.name)
        }

        override fun getBean(): Any = this@Chip

        override fun getName(): String = "deleteGraphic"
    }

    fun deleteGraphicProperty(): ObjectProperty<Node?> = deleteGraphicProperty
    var deleteGraphic: Node? by deleteGraphicProperty()

    init {
        deleteGraphic?.styleClass?.add(Styles.chipDeleteIcon.name)
    }

    override fun getInitialFocusTraversable(): Boolean = false
    override fun getUserAgentStylesheet(): String = Styles().externalForm
    override fun createDefaultSkin(): Skin<*> = ChipSkin(this)
    override fun fire() {
        if (!isDisabled) {
            fireEvent(ActionEvent())
        }
    }

    init {
        addClass(Styles.chip)
    }

    class Styles : Stylesheet() {

        companion object {
            val chip by cssclass()
            val chipRoot by cssclass()
            val clickable by cssclass()
            val chipDeleteIcon by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            chip {
                prefHeight = 32.px
                backgroundRadius += box(16.px)
                borderRadius = multi(box(16.px))

                chipDeleteIcon {
                    fill = javafx.scene.paint.Color.grayRgb(0, 0.26)
                    and(hover) {
                        fill = javafx.scene.paint.Color.grayRgb(0, 0.4)
                    }
                }

                val nonDefaultLabelMixin = mixin {
                    fontWeight = FontWeight.BOLD
                }

                and(ComponentsStyles.filled) {
                    borderWidth = multi(box(0.px))
                    backgroundColor = multi(javafx.scene.paint.Color.web("#e0e0e0"))
                    and(clickable) {
                        and(hover) {
                            backgroundColor = multi(javafx.scene.paint.Color.grayRgb(206))
                        }
                    }

                    label {
                        +nonDefaultLabelMixin
                        textFill = javafx.scene.paint.Color.WHITE
                    }

                    val chipDeleteIconColoredMixin = mixin {
                        fill = javafx.scene.paint.Color.WHITE.deriveColor(1.0, 1.0, 1.0, 0.7)
                        and(hover) {
                            fill = javafx.scene.paint.Color.WHITE
                        }
                    }

                    and(ComponentsStyles.primary) {
                        backgroundColor = multi(com.soyle.stories.soylestories.Styles.Purple)
                        and(clickable) {
                            and(hover) {
                                backgroundColor = multi(com.soyle.stories.soylestories.Styles.Purple.brighter())
                            }
                        }

                        chipDeleteIcon {
                            +chipDeleteIconColoredMixin
                        }
                    }
                    and(ComponentsStyles.secondary) {
                        backgroundColor = multi(com.soyle.stories.soylestories.Styles.Blue)
                        and(clickable) {
                            and(hover) {
                                backgroundColor = multi(com.soyle.stories.soylestories.Styles.Blue.brighter())
                            }
                        }

                        chipDeleteIcon {
                            +chipDeleteIconColoredMixin
                        }
                    }
                }

                and(ComponentsStyles.outlined) {
                    borderWidth = multi(box(1.px))
                    borderColor = multi(box(javafx.scene.paint.Color.web("#e0e0e0")))
                    backgroundColor = multi(javafx.scene.paint.Color.TRANSPARENT)
                    and(clickable) {
                        and(hover) {
                            backgroundColor = multi(javafx.scene.paint.Color.web("#000000", 0.1))
                        }
                    }
                    and(ComponentsStyles.primary) {
                        borderColor = multi(box(com.soyle.stories.soylestories.Styles.Purple))
                        and(clickable) {
                            and(hover) {
                                backgroundColor =
                                    multi(com.soyle.stories.soylestories.Styles.Purple.deriveColor(1.0, 1.0, 1.0, 0.1))
                            }
                        }

                        label {
                            +nonDefaultLabelMixin
                            textFill = com.soyle.stories.soylestories.Styles.Purple
                        }

                        chipDeleteIcon {
                            fill = com.soyle.stories.soylestories.Styles.Purple.deriveColor(1.0, 1.0, 1.0, 0.7)
                            and(hover) {
                                fill = com.soyle.stories.soylestories.Styles.Purple
                            }
                        }
                    }
                    and(ComponentsStyles.secondary) {
                        borderColor = multi(box(com.soyle.stories.soylestories.Styles.Blue))
                        and(clickable) {
                            and(hover) {
                                backgroundColor =
                                    multi(com.soyle.stories.soylestories.Styles.Blue.deriveColor(1.0, 1.0, 1.0, 0.1))
                            }
                        }

                        label {
                            +nonDefaultLabelMixin
                            textFill = com.soyle.stories.soylestories.Styles.Blue
                        }
                        chipDeleteIcon {
                            fill = com.soyle.stories.soylestories.Styles.Blue.deriveColor(1.0, 1.0, 1.0, 0.7)
                            and(hover) {
                                fill = com.soyle.stories.soylestories.Styles.Blue
                            }
                        }
                    }
                }

                chipRoot {
                    alignment = Pos.CENTER
                }

                and(clickable) {
                    cursor = Cursor.HAND
                }

                label {
                    padding = box(0.px, 12.px)
                }

                chipDeleteIcon {
                    cursor = Cursor.HAND
                    fontSize = 1.5.em
                    prefHeight = 22.px
                    prefWidth = 22.px
                    padding = box(0.px, 6.px, 0.px, 0.px)
                }
            }
        }

    }

    enum class Color {
        Default, Primary, Secondary
    }

    enum class Variant {
        Default, Outlined
    }

    companion object {
        @ViewBuilder
        fun EventTarget.chip(
            text: String = "",
            graphic: Node? = null,
            onAction: EventHandler<ActionEvent>? = null,
            onDelete: EventHandler<ActionEvent>? = null,
            deleteGraphic: Node? = null,
            color: Color? = null,
            variant: Variant? = null,
            op: Chip.() -> Unit = {}
        ): Chip {
            val chip = Chip().also {
                it.text = text
                it.graphic = graphic
                it.onAction = onAction
                it.onDelete = onDelete
                if (deleteGraphic != null) it.deleteGraphic = deleteGraphic
                if (color != null) it.color = color
                if (variant != null) it.variant = variant
                it.op()
            }
            addChildIfPossible(chip)
            return chip
        }

        @ViewBuilder
        fun EventTarget.chip(
            textProperty: ObservableValue<String>,
            graphicProperty: ObservableValue<Node?>? = null,
            onActionProperty: ObservableValue<EventHandler<ActionEvent>?>? = null,
            onDeleteProperty: ObservableValue<EventHandler<ActionEvent>?>? = null,
            deleteGraphicProperty: ObservableValue<Node?>? = null,
            colorProperty: ObservableValue<Color>? = null,
            variantProperty: ObservableValue<Variant>? = null,
            op: Chip.() -> Unit = {}
        ): Chip {
            val chip = Chip().also {
                it.textProperty().bind(textProperty)
                if (graphicProperty != null) it.graphicProperty().bind(graphicProperty)
                if (onActionProperty != null) it.onActionProperty().bind(onActionProperty)
                if (onDeleteProperty != null) it.onDeleteProperty().bind(onDeleteProperty)
                if (deleteGraphicProperty != null) it.deleteGraphicProperty().bind(deleteGraphicProperty)
                if (colorProperty != null) it.colorProperty().bind(colorProperty)
                if (variantProperty != null) it.variantProperty().bind(variantProperty)
                it.op()
            }
            addChildIfPossible(chip)
            return chip
        }
    }

}