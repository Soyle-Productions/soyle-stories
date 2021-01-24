package com.soyle.stories.theme.characterConflict

import com.soyle.stories.theme.characterConflict.Styles.Companion.dragAfter
import com.soyle.stories.theme.characterConflict.Styles.Companion.dragBefore
import javafx.scene.Node
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import tornadofx.*

internal val opponentIdFormat = DataFormat("opponent-id")

internal fun Region.disableDragAndDrop() {
    onDragOver = null
    onDragExited = null
    onDragDropped = null
}

internal fun Region.addDragAndDrop() {
    whenOtherCardIsDraggedOver { e, card ->
        val dropPosition = determineValidDropPosition(e, card)
        if (dropPosition != null) e.acceptTransferModes(TransferMode.MOVE)
        styleCardForDrop(dropPosition)
    }
    setOnDragExited {
        clearDragStyleClasses()
    }
    whenOtherCardIsDropped { e, card ->
        val dropPosition = determineValidDropPosition(e, card)
        if (dropPosition != null) e.isDropCompleted = true
        clearDragStyleClasses()
        completeDrag(dropPosition, card)
    }
}

private fun Region.whenOtherCardIsDraggedOver(op: (DragEvent, Node) -> Unit) {
    setOnDragOver {
        it.consume()
        val source = it.gestureSource as? Node ?: return@setOnDragOver
        if (source == this) return@setOnDragOver
        op(it, source)
    }
}

private fun Region.whenOtherCardIsDropped(op: (DragEvent, Node) -> Unit) {
    setOnDragDropped {
        it.consume()
        val source = it.gestureSource as? Node ?: return@setOnDragDropped
        if (source == this) return@setOnDragDropped
        op(it, source)
    }
}

private fun Region.determineValidDropPosition(event: DragEvent, source: Node): DragPosition?
{
    return when {
        event.placingBefore(this) && ! source.isImmediatelyBefore(this) -> DragPosition.Before
        event.placingAfter(this) && ! source.isImmediatelyAfter(this) -> DragPosition.After
        else -> null
    }
}

private fun DragEvent.placingBefore(node: Region): Boolean = y < node.height / 2
private fun DragEvent.placingAfter(node: Region): Boolean = y >= node.height / 2

private fun Node.isImmediatelyBefore(node: Region): Boolean
{
    val nodeIndex = node.indexInParent
    val indexInNodeParent = node.parent?.childrenUnmodifiable?.indexOf(this) ?: return false
    return indexInNodeParent == nodeIndex - 1
}
private fun Node.isImmediatelyAfter(node: Region): Boolean
{
    val nodeIndex = node.indexInParent
    val indexInNodeParent = node.parent?.childrenUnmodifiable?.indexOf(this) ?: return false
    return indexInNodeParent == nodeIndex + 1
}

private fun Node.styleCardForDrop(position: DragPosition?)
{
    clearDragStyleClasses()
    if (position == DragPosition.Before) addClass(dragBefore)
    if (position == DragPosition.After) addClass(dragAfter)
}

private fun Node.clearDragStyleClasses()
{
    if (hasClass(dragBefore)) removeClass(dragBefore)
    if (hasClass(dragAfter)) removeClass(dragAfter)
}

private fun Node.completeDrag(position: DragPosition?, droppedCard: Node) {
    if (position == DragPosition.Before) droppedCard.moveBefore(this)
    if (position == DragPosition.After) droppedCard.moveAfter(this)
}

private fun Node.moveBefore(other: Node)
{
    if (other.indexInParent > indexInParent)
        other.parent.getChildList()?.move(this, other.indexInParent - 1)
    else
        other.parent.getChildList()?.move(this, other.indexInParent)
}
private fun Node.moveAfter(other: Node)
{
    if (other.indexInParent > indexInParent)
        other.parent.getChildList()?.move(this, other.indexInParent)
    else
        other.parent.getChildList()?.move(this, other.indexInParent + 1)
}

private enum class DragPosition {
    Before, After
}

class Styles : Stylesheet() {
    companion object {
        val dragBefore by cssclass()
        val dragAfter by cssclass()
        init {
            importStylesheet(Styles::class)
        }
    }

    init {
        dragBefore {
            borderWidth += box(3.px, 0.px, 0.px, 0.px)
            borderColor += box(com.soyle.stories.soylestories.Styles.Blue)
        }
        dragAfter {
            borderWidth += box(0.px, 0.px, 3.px, 0.px)
            borderColor += box(com.soyle.stories.soylestories.Styles.Blue)
        }
    }
}