package com.soyle.stories.ramifications

import com.sun.javafx.event.EventHandlerManager
import javafx.beans.property.*
import javafx.event.*
import javafx.scene.Node
import javafx.scene.control.Tab
import tornadofx.getValue
import tornadofx.setValue

class Report : EventTarget {

    companion object {
        @JvmStatic
        val REPORT_CLOSE_REQUEST_EVENT = EventType(Event.ANY, "REPORT_CLOSE_REQUEST_EVENT")
    }

    private val _text by lazy { SimpleStringProperty(this, "text") }
    fun text(): StringProperty = _text
    var text: String by _text

    private val _graphic by lazy { SimpleObjectProperty<Node?>(this, "graphic", null) }
    fun graphic(): ObjectProperty<Node?> = _graphic
    var graphic: Node? by _graphic

    private val _content by lazy { SimpleObjectProperty<Node?>(this, "content", null) }
    fun content(): ObjectProperty<Node?> = _content
    var content: Node? by _content

    private val _isListed by lazy { SimpleBooleanProperty(this, "isListed") }
    fun isListed(): BooleanProperty = _isListed
    var isListed: Boolean by _isListed

    /**
     * Called when there is an external request to close this `Report`.
     * The installed event handler can prevent tab closing by consuming the
     * received event.
     */
    private val _onCloseRequest: ObjectProperty<EventHandler<Event>> by lazy {
        object : ObjectPropertyBase<EventHandler<Event>>() {
            override fun getBean(): Any = this@Report
            override fun getName(): String = "onCloseRequest"
            override fun invalidated() {
                setEventHandler(REPORT_CLOSE_REQUEST_EVENT, get())
            }
        }
    }
    fun onCloseRequest(): ObjectProperty<EventHandler<Event>> = _onCloseRequest
    var onCloseRequest: EventHandler<Event>? by _onCloseRequest
        @JvmName("assignOnCloseRequest")
        set
    fun setOnCloseRequest(handler: EventHandler<Event>) { onCloseRequest = handler }

    fun close(): Boolean {
        val event = Event(REPORT_CLOSE_REQUEST_EVENT)
        Event.fireEvent(this, event)
        return ! event.isConsumed
    }

    private val eventHandlerManager = EventHandlerManager(this)

    override fun buildEventDispatchChain(tail: EventDispatchChain): EventDispatchChain {
        return tail.prepend(eventHandlerManager)
    }

    fun <E : Event?> setEventHandler(eventType: EventType<E>, eventHandler: EventHandler<E>?) {
        eventHandlerManager.setEventHandler(eventType, eventHandler)
    }

}