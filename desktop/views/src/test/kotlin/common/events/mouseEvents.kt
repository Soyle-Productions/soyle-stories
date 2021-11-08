package com.soyle.stories.desktop.view.common.events

import javafx.event.EventTarget
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.PickResult

fun pickResult(
    target: EventTarget? = null,
    x: Double = 0.0,
    y: Double = 0.0
): PickResult = PickResult(
    target,
    x,
    y
)

fun dragDetectedEvent(
    x: Double = 0.0,
    y: Double = 0.0,
    pickResult: PickResult? = null,
    screenX: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.x ?: x,
    screenY: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.y ?: y,
    button: MouseButton = MouseButton.PRIMARY,
    clickCount: Int = 1,
    shiftDown: Boolean = false,
    controlDown: Boolean = false,
    altDown: Boolean = false,
    metaDown: Boolean = false,
    primaryButtonDown: Boolean = button == MouseButton.PRIMARY,
    middleButtonDown: Boolean = button == MouseButton.MIDDLE,
    secondaryButtonDown: Boolean = button == MouseButton.SECONDARY,
    synthesized: Boolean = false,
    popupTrigger: Boolean = false,
    stillSincePress: Boolean = false
) = MouseEvent(
    MouseEvent.DRAG_DETECTED,
    x,
    y,
    screenX,
    screenY,
    button,
    clickCount,
    shiftDown,
    controlDown,
    altDown,
    metaDown,
    primaryButtonDown,
    middleButtonDown,
    secondaryButtonDown,
    synthesized,
    popupTrigger,
    stillSincePress,
    pickResult
)


fun mouseDraggedEvent(
    x: Double = 0.0,
    y: Double = 0.0,
    pickResult: PickResult? = null,
    screenX: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.x ?: x,
    screenY: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.y ?: y,
    button: MouseButton = MouseButton.PRIMARY,
    clickCount: Int = 1,
    shiftDown: Boolean = false,
    controlDown: Boolean = false,
    altDown: Boolean = false,
    metaDown: Boolean = false,
    primaryButtonDown: Boolean = button == MouseButton.PRIMARY,
    middleButtonDown: Boolean = button == MouseButton.MIDDLE,
    secondaryButtonDown: Boolean = button == MouseButton.SECONDARY,
    synthesized: Boolean = false,
    popupTrigger: Boolean = false,
    stillSincePress: Boolean = false
) = MouseEvent(
    MouseEvent.MOUSE_DRAGGED,
    x,
    y,
    screenX,
    screenY,
    button,
    clickCount,
    shiftDown,
    controlDown,
    altDown,
    metaDown,
    primaryButtonDown,
    middleButtonDown,
    secondaryButtonDown,
    synthesized,
    popupTrigger,
    stillSincePress,
    pickResult
)

fun mouseReleasedEvent(
    source: Any? = null,
    target: EventTarget? = null,
    x: Double = 0.0,
    y: Double = 0.0,
    pickResult: PickResult? = null,
    screenX: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.x ?: x,
    screenY: Double = pickResult?.intersectedNode?.localToScreen(x, y)?.y ?: y,
    button: MouseButton = MouseButton.PRIMARY,
    clickCount: Int = 1,
    shiftDown: Boolean = false,
    controlDown: Boolean = false,
    altDown: Boolean = false,
    metaDown: Boolean = false,
    primaryButtonDown: Boolean = button == MouseButton.PRIMARY,
    middleButtonDown: Boolean = button == MouseButton.MIDDLE,
    secondaryButtonDown: Boolean = button == MouseButton.SECONDARY,
    synthesized: Boolean = false,
    popupTrigger: Boolean = false,
    stillSincePress: Boolean = false,
) = MouseEvent(
    source,
    target,
    MouseEvent.MOUSE_RELEASED,
    x,
    y,
    screenX,
    screenY,
    button,
    clickCount,
    shiftDown,
    controlDown,
    altDown,
    metaDown,
    primaryButtonDown,
    middleButtonDown,
    secondaryButtonDown,
    synthesized,
    popupTrigger,
    stillSincePress,
    pickResult,
)