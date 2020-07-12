package com.soyle.stories.common.components

import com.soyle.stories.common.onChangeUntil
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.*
import tornadofx.*
import javax.swing.Action

class PopOutEditBox(
    val node: Node,
    private val textProperty: ObservableValue<String>,
    val preferSingleLine: Boolean = true
) : Stage(StageStyle.UNDECORATED){

    companion object {
        private val helperText = Text("")
        private val paddingSize = 2.0
    }

    val textInput: TextInputControl = if (preferSingleLine) textfield {
        this.setOnAction {
            println("popup textinput action")
            it.consume()
            this@PopOutEditBox.commit()
        }
    } else textarea {
        prefRowCount = 1
    }

    init {
        scene = Scene(HBox(textInput))
        FX.applyStylesheetsTo(scene)
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) cancel()
        }
        initModality(Modality.NONE)
        scene.fill = Color.TRANSPARENT

        textInput.prefWidthProperty().onChange {
            runLater {
                sizeToScene()
            }
        }
        textInput.textProperty().onChange {
            helperText.text = it ?: ""
            textInput.prefWidthProperty().set(calculateTextInputPrefWidth())
        }
        textInput.paddingLeftProperty.onChange { textInput.prefWidthProperty().set(calculateTextInputPrefWidth()) }
        textInput.paddingRightProperty.onChange { textInput.prefWidthProperty().set(calculateTextInputPrefWidth()) }
    }

    private fun calculateTextInputPrefWidth(): Double
    {
        return helperText.layoutBounds.width + textInput.padding.left + textInput.padding.right + 2.0
    }

    private fun commit() {
        println("popout commit")
        fireEvent(ActionEvent())
    }

    fun complete() {
        requestClose()
        commit()
    }

    private fun cancel() {
        requestClose()
    }

    private fun requestClose() {
        val e = WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST)
        fireEvent(e)
        if (! e.isConsumed) {
            hide()
        }
    }

    private val onActionProperty: ObjectProperty<EventHandler<ActionEvent>> =
        object : ObjectPropertyBase<EventHandler<ActionEvent>>() {
            override fun invalidated() {
                setEventHandler(ActionEvent.ACTION, get())
            }

            override fun getBean(): Any {
                return this@PopOutEditBox
            }

            override fun getName(): String {
                return "onAction"
            }
        }

    inline fun setOnAction(noinline value: (ActionEvent) -> Unit) { onAction = EventHandler(value) }

    var onAction: EventHandler<ActionEvent>? by onActionProperty()

    fun onActionProperty(): ObjectProperty<EventHandler<ActionEvent>> {
        return onActionProperty
    }

    fun popup() {
        node.localToScreen(node.boundsInLocal)?.let {
            setAvailableBounds(it)
        }

        if (owner == null) {
            val nodeScene = node.scene
            if (nodeScene != null) {
                initOwner(nodeScene.window)
                show()
            } else {
                node.sceneProperty().onChangeUntil({ node.scene != null || isShowing }) {
                    if (owner == null && it != null) {
                        initOwner(it.window)
                        show()
                    }
                }
            }
        } else {
            show()
        }

        node.boundsInLocalProperty().onChangeUntil({ !isShowing }) { bounds ->
            if (bounds == null || ! isShowing) return@onChangeUntil
            val nodeBounds = node.localToScreen(bounds)
            setAvailableBounds(nodeBounds)
        }
        helperText.font = textInput.font
        textInput.text = textProperty.value

        textInput.requestFocus()
        focusedProperty().onChangeUntil({ !isShowing }) {
            if (it == false && isShowing) {
                complete()
            }
        }
        textInput.selectAll()

    }

    private fun setAvailableBounds(nodeBounds: Bounds) {
        textInput.minWidth = nodeBounds.width
        textInput.minHeight = nodeBounds.height
        x = nodeBounds.minX
        y = nodeBounds.minY

        val screen = Screen.getScreensForRectangle(nodeBounds.minX, nodeBounds.minY, nodeBounds.width, nodeBounds.height).find {
            it.bounds.contains(nodeBounds.minX, nodeBounds.minY)
        } ?: throw Error("Node is not on any screen.  $node $nodeBounds")

        textInput.maxWidth = screen.bounds.width - nodeBounds.minX
        textInput.maxHeight = screen.bounds.height - nodeBounds.minY
    }
}


fun Node.popOutEditBox(initialText: String, op: PopOutEditBox.() -> Unit = {}) = popOutEditBox(initialText.toProperty(), op)
fun Node.popOutEditBox(textProperty: ObservableValue<String>, op: PopOutEditBox.() -> Unit = {}): PopOutEditBox {
    val popOutEditBox = this.popOutEditBox ?: PopOutEditBox(this, textProperty)
    popOutEditBox.op()
    this.popOutEditBox = popOutEditBox
    return popOutEditBox
}

var Node.popOutEditBox: PopOutEditBox?
    get() = properties["com.soyle.stories.popOutEditBox"] as? PopOutEditBox
    set(value) {
        properties["com.soyle.stories.popOutEditBox"] = value
    }