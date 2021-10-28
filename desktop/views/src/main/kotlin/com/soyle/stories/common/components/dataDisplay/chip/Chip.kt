package com.soyle.stories.common.components.dataDisplay.chip

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.*
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
import tornadofx.Stylesheet

open class Chip : ButtonBase() {

    val node: Node = this

    init {
        onActionProperty().onChange {
            this@Chip.isFocusTraversable = it != null
            toggleClass(Styles.clickable, it != null)
        }
    }

    private val colorProperty: ObjectProperty<Color> = object : StyleableObjectProperty<Color>(Color.default) {
        override fun getCssMetaData(): CssMetaData<out Styleable, Color> = COLOR
        override fun getBean(): Any = this@Chip
        override fun getName(): String = "color"
    }
    fun colorProperty(): ObjectProperty<Color> = colorProperty
    var color: Color by colorProperty()

    private val variantProperty: ObjectProperty<Variant> = object : StyleableObjectProperty<Variant>(Variant.default) {
        override fun getCssMetaData(): CssMetaData<out Styleable, Variant> = VARIANT
        override fun getBean(): Any = this@Chip
        override fun getName(): String = "variant"
        override fun invalidated() {
            super.invalidated()
        }
    }
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
        override fun getBean(): Any = this@Chip

        override fun getName(): String = "deleteGraphic"
    }

    fun deleteGraphicProperty(): ObjectProperty<Node?> = deleteGraphicProperty
    var deleteGraphic: Node? by deleteGraphicProperty()

    init {
        deleteGraphic?.styleClass?.add(Styles.chipDeleteIcon.name)
    }

    override fun getInitialFocusTraversable(): Boolean = false
    override fun getUserAgentStylesheet(): String = Styles().base64URL.toExternalForm()
    override fun getControlCssMetaData(): MutableList<CssMetaData<out Styleable, *>> = Chip.Companion.cssMetaData
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

            var PropertyHolder.chipColor: Chip.Color
                get() = properties[Chip.COLOR.property]?.first as Chip.Color
                set(value) {
                    properties[Chip.COLOR.property] = value as Any to properties[Chip.COLOR.property]?.second
                }

            var PropertyHolder.chipVariant: Chip.Variant
                get() = properties[Chip.VARIANT.property]?.first as Chip.Variant
                set(value) {
                    properties[Chip.VARIANT.property] = value as Any to properties[Chip.VARIANT.property]?.second
                }

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
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
                        backgroundColor = multi(ColorStyles.secondaryColor)
                        and(clickable) {
                            and(hover) {
                                backgroundColor = multi(ColorStyles.secondaryColor.brighter())
                            }
                        }

                        chipDeleteIcon {
                            +chipDeleteIconColoredMixin
                        }
                    }
                    and(ComponentsStyles.secondary) {
                        backgroundColor = multi(ColorStyles.primaryColor)
                        and(clickable) {
                            and(hover) {
                                backgroundColor = multi(ColorStyles.primaryColor.brighter())
                            }
                        }

                        chipDeleteIcon {
                            +chipDeleteIconColoredMixin
                        }
                    }
                }

                and(ComponentsStyles.outlined) {
                    backgroundColor = multi(javafx.scene.paint.Color.TRANSPARENT)
                    borderWidth = multi(box(1.px))
                    borderColor = multi(box(ColorStyles.lightHighlightColor))
                    and(clickable) {
                        and(hover) {
                        }
                    }
                    and(ComponentsStyles.primary) {
                        borderColor = multi(box(ColorStyles.secondaryColor))
                        and(clickable) {
                            and(hover) {
                                backgroundColor =
                                    multi(com.soyle.stories.soylestories.Styles.Purple.deriveColor(1.0, 1.0, 1.0, 0.1))
                            }
                        }

                        label {
                            +nonDefaultLabelMixin
                            textFill = ColorStyles.secondaryColor
                        }

                        chipDeleteIcon {
                            fill = ColorStyles.secondaryColor.deriveColor(1.0, 1.0, 1.0, 0.7)
                            and(hover) {
                                fill = ColorStyles.secondaryColor
                            }
                        }
                    }
                    and(ComponentsStyles.secondary) {
                        borderColor = multi(box(ColorStyles.primaryColor))
                        and(clickable) {
                            and(hover) {
                                backgroundColor =
                                    multi(ColorStyles.primaryColor.deriveColor(1.0, 1.0, 1.0, 0.1))
                            }
                        }

                        label {
                            +nonDefaultLabelMixin
                            textFill = ColorStyles.primaryColor
                        }
                        chipDeleteIcon {
                            fill = ColorStyles.primaryColor.deriveColor(1.0, 1.0, 1.0, 0.7)
                            and(hover) {
                                fill = ColorStyles.primaryColor
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
        default, primary, secondary
    }

    enum class Variant {
        default, outlined
    }

    companion object {

        private val COLOR: CssMetaData<Chip, Color> = object : CssMetaData<Chip, Color>("-fx-chip-color", StyleConverter.getEnumConverter(Chip.Color::class.java)) {
            override fun isSettable(styleable: Chip?): Boolean {
                return styleable?.color == Color.default || styleable?.colorProperty?.isBound != true
            }

            override fun getStyleableProperty(styleable: Chip?): StyleableProperty<Color> {
                return styleable!!.colorProperty as StyleableProperty<Color>
            }
        }

        private val VARIANT: CssMetaData<Chip, Variant> = object : CssMetaData<Chip, Variant>("-fx-chip-variant", StyleConverter.getEnumConverter(Chip.Variant::class.java)) {
            override fun isSettable(styleable: Chip?): Boolean {
                return styleable?.variantProperty?.isBound != true
            }

            override fun getStyleableProperty(styleable: Chip?): StyleableProperty<Variant> {
                return styleable!!.variantProperty as StyleableProperty<Variant>
            }
        }

        private val cssMetaData: MutableList<CssMetaData<out Styleable, *>> = mutableListOf<CssMetaData<out Styleable, *>>().apply {
            addAll(ButtonBase.getClassCssMetaData())
            addAll(listOf(
                COLOR,
                VARIANT
            ))
        }

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