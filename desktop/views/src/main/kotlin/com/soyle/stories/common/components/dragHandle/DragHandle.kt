package com.soyle.stories.common.components.dragHandle

import com.soyle.stories.common.components.dragHandle.DragHandleImpl.Styles.Companion.keyboardMove
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.effect.Glow
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import tornadofx.*

interface DragHandle {

    var targetNode: Node?
    var transferModes: List<TransferMode>
    var clipboardContent: (ClipboardContent.() -> Unit)?
    var onDragDetected: () -> Unit

    var onMoveStart: () -> Unit
    var onMoveDone: () -> Unit
    var onMoveCancelled: () -> Unit

    var onMoveUp: () -> Unit
    var onMoveDown: () -> Unit

}

class DragHandleImpl : Fragment(), DragHandle {

    companion object {
        fun Parent.dragHandle(op: DragHandle.() -> Unit = {}): Node
        {
            val dragHandle = find<DragHandleImpl>()
            add(dragHandle.root)
            dragHandle.op()
            return dragHandle.root
        }
    }

    override var targetNode: Node? = null
    override var transferModes: List<TransferMode> = listOf()
    override var clipboardContent: (ClipboardContent.() -> Unit)? = null
    override var onDragDetected: () -> Unit = {}

    override var onMoveStart: () -> Unit = {}
    override var onMoveDone: () -> Unit = {}
    override var onMoveCancelled: () -> Unit = {}

    override var onMoveDown: () -> Unit = {}
    override var onMoveUp: () -> Unit = {}

    override val root: Parent = label {
        graphic = MaterialIconView(MaterialIcon.DRAG_HANDLE, "16px")
        addClass(Styles.dragHandle)
        isFocusTraversable = true

        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (determineMoveBehaviorFromKeyCode(it.code)) {
                it.consume()
            }
        }
        setOnDragDetected { e ->
            if (transferModes.isNotEmpty()) {
                val draggedNode = targetNode ?: return@setOnDragDetected
                val board = draggedNode.startDragAndDrop(*transferModes.toTypedArray())

                clipboardContent?.let {
                    board.setContent(it)
                }

                val snapshot = draggedNode.snapshot(SnapshotParameters(), null)
                board.setDragView(
                    snapshot,
                    snapshot.width - width + e.x,
                    (snapshot.height / 2)
                )

                onDragDetected()

                e.consume()
            }
        }
    }

    private fun determineMoveBehaviorFromKeyCode(code: KeyCode): Boolean
    {
        when (code) {
            KeyCode.ESCAPE -> endMove()
            KeyCode.ENTER -> startOrCommitMove()
            KeyCode.UP, KeyCode.KP_UP -> moveUp()
            KeyCode.DOWN, KeyCode.KP_DOWN -> moveDown()
            else -> return false
        }
        return true
    }

    private var isMoving = false

    private fun endMove() {
        if (! isMoving) return
        isMoving = false
        root.removeClass(keyboardMove)
        onMoveCancelled()
    }
    private fun startMove() {
        println("startMove")
        isMoving = true
        root.addClass(keyboardMove)
        onMoveStart()
    }
    private fun commitMove() {
        println("commit move")
        if (! isMoving) return
        isMoving = false
        root.removeClass(keyboardMove)
        onMoveDone()
    }
    private fun startOrCommitMove() {
        if (isMoving) commitMove() else startMove()
    }
    private fun moveUp() {
        if (! isMoving) return
        onMoveUp()
    }
    private fun moveDown() {
        if (! isMoving) return
        onMoveDown()
    }

    class Styles : Stylesheet() {
        companion object {

            val dragHandle by cssclass()
            val keyboardMove by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            dragHandle {
                and(focused) {
                    borderWidth = multi(box(1.px))
                    borderInsets = multi(box((-1).px))
                    borderColor = multi(box(Color.BLUE))
                }
                and(keyboardMove) {
                    borderWidth = multi(box(1.px))
                    borderInsets = multi(box((-1).px))
                    borderColor = multi(box(Color.RED))
                }
            }
        }
    }

}