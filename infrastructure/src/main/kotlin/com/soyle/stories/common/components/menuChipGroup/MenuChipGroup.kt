package com.soyle.stories.common.components.menuChipGroup

import com.soyle.stories.common.components.Chip
import com.soyle.stories.common.components.ChipNode
import com.sun.javafx.collections.TrackableObservableList
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.SetChangeListener
import javafx.css.PseudoClass
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.AccessibleAction
import javafx.scene.AccessibleRole
import javafx.scene.control.*
import tornadofx.*

class MenuChipGroup : ButtonBase() {

    companion object {
        val ON_SHOWING = EventType(Event.ANY, "MENU_CHIP_GROUP_ON_SHOWING")
        val ON_SHOWN = EventType(Event.ANY, "MENU_CHIP_GROUP_ON_SHOWN")
        val ON_HIDING = EventType(Event.ANY, "MENU_CHIP_GROUP_ON_HIDING")
        val ON_HIDDEN = EventType(Event.ANY, "MENU_CHIP_GROUP_ON_HIDDEN")
    }

    val chips = observableListOf<Chip>()

    val items = observableListOf<MenuItem>()

    val noSelectionTextProperty = SimpleStringProperty(this, "noSelectionText", "")
    var noSelectionText: String by noSelectionTextProperty


    // --- Showing
    /**
     * Indicates whether the [ContextMenu] is currently visible.
     */
    private val showingProperty: ReadOnlyBooleanWrapper = object : ReadOnlyBooleanWrapper(this, "showing", false) {
        override fun invalidated() {
            pseudoClassStateChanged(PSEUDO_CLASS_SHOWING, get())
            super.invalidated()
        }
    }
    var isShowing: Boolean
        get() = showingProperty.get()
        private set(value) {
            Event.fireEvent(this, if (value) Event(ON_SHOWING) else Event(ON_HIDING))
            showingProperty.set(value)
            Event.fireEvent(this, if (value) Event(ON_SHOWN) else Event(ON_HIDDEN))
        }

    fun showingProperty(): ReadOnlyBooleanProperty {
        return showingProperty.readOnlyProperty
    }

    /**
     * Called just prior to the `ContextMenu` being shown.
     * @return the on showing property
     */
    private val onShowingProperty: ObjectProperty<EventHandler<Event>?> =
        object : ObjectPropertyBase<EventHandler<Event>?>() {
            override fun invalidated() {
                setEventHandler(ON_SHOWING, get())
            }

            override fun getBean(): Any {
                return this@MenuChipGroup
            }

            override fun getName(): String {
                return "onShowing"
            }
        }

    fun onShowingProperty(): ObjectProperty<EventHandler<Event>?> = onShowingProperty
    var onShowing: EventHandler<Event>? by onShowingProperty()
    fun onShowing(handler: EventHandler<Event>) {
        onShowing = handler
    }

    /**
     * Called just after the `ContextMenu` is shown.
     * @return the on shown property
     */
    private val onShownProperty: ObjectProperty<EventHandler<Event>?> =
        object : ObjectPropertyBase<EventHandler<Event>?>() {
            override fun invalidated() {
                setEventHandler(ON_SHOWN, get())
            }

            override fun getBean(): Any {
                return this@MenuChipGroup
            }

            override fun getName(): String {
                return "onShown"
            }
        }

    fun onShownProperty(): ObjectProperty<EventHandler<Event>?> = onShownProperty
    var onShown: EventHandler<Event>? by onShownProperty()
    fun onShown(handler: EventHandler<Event>) {
        onShown = handler
    }

    /**
     * Called just prior to the `ContextMenu` being hidden.
     * @return the on hiding property
     */
    private val onHidingProperty: ObjectProperty<EventHandler<Event>?> =
        object : ObjectPropertyBase<EventHandler<Event>?>() {
            override fun invalidated() {
                setEventHandler(ON_HIDING, get())
            }

            override fun getBean(): Any {
                return this@MenuChipGroup
            }

            override fun getName(): String {
                return "onHiding"
            }
        }

    fun onHidingProperty(): ObjectProperty<EventHandler<Event>?> = onHidingProperty
    var onHiding: EventHandler<Event>? by onHidingProperty()
    fun onHiding(handler: EventHandler<Event>) {
        onHiding = handler
    }

    /**
     * Called just after the `ContextMenu` has been hidden.
     * @return the on hidden property
     */
    private val onHiddenProperty: ObjectProperty<EventHandler<Event>?> =
        object : ObjectPropertyBase<EventHandler<Event>?>() {
            override fun invalidated() {
                setEventHandler(ON_HIDDEN, get())
            }

            override fun getBean(): Any {
                return this@MenuChipGroup
            }

            override fun getName(): String {
                return "onHidden"
            }
        }

    fun onHiddenProperty(): ObjectProperty<EventHandler<Event>?> = onHiddenProperty
    var onHidden: EventHandler<Event>? by onHiddenProperty()
    fun onHidden(handler: EventHandler<Event>) {
        onHidden = handler
    }

    fun show() {
        if (!isDisabled && !showingProperty.isBound) {
            isShowing = true
        }
    }

    fun hide() {
        if (!showingProperty.isBound) {
            isShowing = false
        }
    }

    override fun fire() {
        if (!isDisabled) {
            fireEvent(ActionEvent())
        }
    }

    private val DEFAULT_STYLE_CLASS = MenuChipGroupStyles.menuChipGroup
    private val PSEUDO_CLASS_SHOWING = PseudoClass.getPseudoClass(MenuChipGroupStyles.showing.name)

    /** {@inheritDoc}  */
    override fun executeAccessibleAction(action: AccessibleAction?, vararg parameters: Any?) {
        when (action) {
            AccessibleAction.FIRE -> if (isShowing) {
                hide()
            } else {
                show()
            }
            else -> super.executeAccessibleAction(action)
        }
    }

    override fun createDefaultSkin(): Skin<*> = MenuChipGroupSkin(this)

    init {
        addClass(DEFAULT_STYLE_CLASS)
        isFocusTraversable = true
        accessibleRole = AccessibleRole.MENU_BUTTON
    }

}