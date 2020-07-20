package com.soyle.stories.common.components

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.event.*
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContentDisplay
import javafx.scene.control.MenuButton
import javafx.scene.paint.Color
import tornadofx.*

class EditableText : Fragment() {

    companion object {
        /**
         * Called prior to the EditableText showing its popup after the user
         * has clicked or otherwise interacted with the EditableText.
         * @since JavaFX 8u60
         */
        val ON_SHOWING = EventType(Event.ANY, "EDITABLE_TEXT_ON_SHOWING")

        /**
         * Called after the EditableText has shown its popup.
         * @since JavaFX 8u60
         */
        val ON_SHOWN = EventType(Event.ANY, "EDITABLE_TEXT_ON_SHOWN")

        /**
         * Called when the EditableText popup **will** be hidden.
         * @since JavaFX 8u60
         */
        val ON_HIDING = EventType(Event.ANY, "EDITABLE_TEXT_ON_HIDING")

        /**
         * Called when the EditableText popup has been hidden.
         * @since JavaFX 8u60
         */
        val ON_HIDDEN = EventType(Event.ANY, "EDITABLE_TEXT_ON_HIDDEN")


        const val DEFAULT_STYLE_CLASS = "editable-text"
        private val PSEUDO_CLASS_SHOWING = PseudoClass.getPseudoClass("showing")
    }

    val textProperty: StringProperty = titleProperty
    var text: String? by textProperty

    val graphicProperty = iconProperty
    var graphic: Node? by graphicProperty

    val placeholderTextProperty: StringProperty = SimpleStringProperty(this, "placeholderText", "")
    var placeholderText: String? by placeholderTextProperty

    private val editedTextProperty: ReadOnlyStringWrapper = ReadOnlyStringWrapper(this, "editedText", "")
    fun editedTextProperty(): ReadOnlyStringProperty = editedTextProperty.readOnlyProperty
    var editedText: String?
        get() = editedTextProperty.get()
        private set(value) {
            editedTextProperty.set(value)
        }

    val errorMessageProperty: StringProperty = SimpleStringProperty(this, "errorMessage", "")
    var errorMessage: String? by errorMessageProperty

    private val showingProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(this, "showing", false)
    fun showingProperty(): ReadOnlyBooleanProperty = showingProperty.readOnlyProperty
    var isShowing: Boolean
        get() = showingProperty.get()
        private set(value) {
            if (value) onShowing?.handle(Event(ON_SHOWING)) else onHiding?.handle(Event(ON_HIDING))
            showingProperty.set(value)
            if (value) onShown?.handle(Event(ON_SHOWN)) else onHidden?.handle(Event(ON_HIDDEN))
        }

    val onShowingProperty: ObjectProperty<EventHandler<Event>> = SimpleObjectProperty(this, "onShowing", null)
    var onShowing: EventHandler<Event>? by onShowingProperty
    fun onShowing(value: (Event) -> Unit) { onShowing = EventHandler(value) }

    val onHidingProperty: ObjectProperty<EventHandler<Event>> = SimpleObjectProperty(this, "onHiding", null)
    var onHiding: EventHandler<Event>? by onHidingProperty
    fun onHiding(value: (Event) -> Unit) { onHiding = EventHandler(value) }

    val onShownProperty: ObjectProperty<EventHandler<Event>> = SimpleObjectProperty(this, "onShown", null)
    var onShown: EventHandler<Event>? by onShownProperty
    fun onShown(value: (Event) -> Unit) { onShown = EventHandler(value) }

    val onHiddenProperty: ObjectProperty<EventHandler<Event>> = SimpleObjectProperty(this, "onHidden", null)
    var onHidden: EventHandler<Event>? by onHiddenProperty
    fun onHidden(value: (Event) -> Unit) { onHidden = EventHandler(value) }

    override val root: Parent = hyperlink {
        this@EditableText.textProperty.select { it.isNullOrBlank().toProperty() }.onChange {
            if (it != true) textProperty().cleanBind(this@EditableText.textProperty) else textProperty().cleanBind(this@EditableText.placeholderTextProperty)
        }
        if (! this@EditableText.text.isNullOrBlank()) textProperty().cleanBind(this@EditableText.textProperty) else textProperty().cleanBind(this@EditableText.placeholderTextProperty)
        addClass(DEFAULT_STYLE_CLASS)
        graphicProperty().bind(this@EditableText.graphicProperty)
        contentDisplay = ContentDisplay.RIGHT

        showingProperty.onChange {
            togglePseudoClass(PSEUDO_CLASS_SHOWING.pseudoClassName, it)
        }
        action {
            this@EditableText.show()
        }
    }

    fun focusedProperty() = root.focusedProperty()
    val isFocused get() = root.isFocused

    var id by root.idProperty()

    private val popup = root.popOutEditBox(textProperty) {
        editedTextProperty.bind(textInput.textProperty())
        setOnHidden {
            this@EditableText.hide()
        }
        setOnShown {
            this@EditableText.show()
        }
    }

    init {
        showingProperty.onChange {
            if (it && ! popup.isShowing) {
                popup.popup()
                if (errorMessage != null) {
                    displayError(popup.textInput)
                }
            }
            if (!it && popup.isShowing) {
                popup.hide()
                if (errorMessage != null) {
                    displayError(root)
                }
            }
        }
        errorMessageProperty.onChange {
            if (it == null) clearError(popup.textInput, root)
            else {
                if (isShowing) displayError(popup.textInput)
                else displayError(root)
            }
        }
        textProperty.onChange {
            styleBasedOnText(it)
        }
        styleBasedOnText(text)
    }

    private fun styleBasedOnText(text: String?) {
        if (text.isNullOrBlank()) {
            root.style {
                textFill = Color.GREY
            }
        } else {
            root.style = ""
        }
    }

    private val onActionProperty: ObjectProperty<EventHandler<ActionEvent>> = popup.onActionProperty()
    inline fun setOnAction(noinline value: (ActionEvent) -> Unit) { onAction = EventHandler(value) }
    var onAction: EventHandler<ActionEvent>? by onActionProperty()
    fun onActionProperty(): ObjectProperty<EventHandler<ActionEvent>> = popup.onActionProperty()

    fun show() {
        if (! root.isDisabled && !showingProperty.isBound) {
            isShowing = true
        }
    }

    fun hide() {
        if (!showingProperty.isBound) {
            isShowing = false
        }
    }

    private fun clearError(vararg nodes: Node) {
        nodes.forEach { decoratedNode ->
            decoratedNode.decorators.toList().forEach { it.undecorate(decoratedNode) }
        }
    }

    private fun displayError(decoratedNode: Node) {
        decoratedNode.decorators.toList().forEach { it.undecorate(decoratedNode) }
        val errorMessage = this.errorMessage
        if (errorMessage != null && errorMessage.isNotBlank()) {
            decoratedNode.addDecorator(SimpleMessageDecorator(errorMessage, ValidationSeverity.Error))
        }
    }

}

fun EventTarget.editableText(text: String = "", op: EditableText.() -> Unit = {}) = find<EditableText>(FX.defaultScope)
    .apply {
        this.text = text
        op()
    }
    .also {
        opcr(this, it.root)
    }
fun EventTarget.editableText(textProperty: ObservableValue<String>, op: EditableText.() -> Unit = {}): EditableText = find<EditableText>(FX.defaultScope)
    .apply {
        this.textProperty.bind(textProperty)
        op()
    }
    .also {
        opcr(this, it.root)
    }