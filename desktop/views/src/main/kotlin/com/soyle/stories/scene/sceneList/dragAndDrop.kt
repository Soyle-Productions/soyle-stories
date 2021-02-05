package com.soyle.stories.scene.sceneList

import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.Styles
import javafx.scene.SnapshotParameters
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import tornadofx.*

private val idFormat = DataFormat("entity/scene-id")
private val nameFormat = DataFormat("entity/scene-name")
private val indexFormat = DataFormat("entity/scene-index")

fun TreeCell<SceneItemViewModel?>.enableDrag()
{
	setOnDragDetected {
		setDraggingCell()
		setDraggingStyle()
		it.consume()
	}
	setOnDragDone {
		removeDraggingStyle()
		it.consume()
	}
}
fun TreeCell<SceneItemViewModel?>.enableDrop(consumer: (String, String, Int) -> Unit)
{
	setOnDragOver {
		style = ""
		it.consume()
		if (it.wouldDropAtStartingPlace(this)) return@setOnDragOver
		acceptDragDrop(it)
	}
	setOnDragExited {
		style = ""
		it.consume()
	}
	setOnDragDropped {
		it.isDropCompleted = true
		if (it.wouldDropAtStartingPlace(this)) return@setOnDragDropped
		val dropIndex = dropIndex(this, it)
		consumer(
		  it.dragboard.getContent(idFormat).toString(),
		  it.dragboard.getContent(nameFormat).toString(),
		  dropIndex
		)
	}
}
private fun TreeCell<SceneItemViewModel?>.setDraggingCell() {
	val item = treeItem.value ?: return
	val dragBoard = startDragAndDrop(TransferMode.MOVE)
	dragBoard.setContent(ClipboardContent().apply {
		put(idFormat, item.id)
		put(nameFormat, item.name)
		put(indexFormat, item.index)
	})
	style {
		borderColor += box(Color.BLACK)
		borderWidth += box(1.0.px)
		backgroundColor += Color.WHITE
		textFill = Color.BLACK
	}
	dragBoard.dragView = snapshot(SnapshotParameters(), null)
	style = ""
}

private fun TreeCell<SceneItemViewModel?>.setDraggingStyle() {
	addClass(SceneListView.Styles.draggingCell)
	graphic?.isVisible = false
}
private fun TreeCell<SceneItemViewModel?>.removeDraggingStyle() {
	removeClass(SceneListView.Styles.draggingCell)
	graphic?.isVisible = true
}
private fun DragEvent.wouldDropAtStartingPlace(receivingCell: TreeCell<SceneItemViewModel?>): Boolean
{
	val item = receivingCell.treeItem.value ?: return false
	if (item.id == dragboard.getContent(idFormat)) return true
	val dropIndex = dropIndex(receivingCell, this)
	if (dropIndex == dragboard.getContent(indexFormat)
	  || dropIndex - 1 == dragboard.getContent(indexFormat)) return true
	return false
}
private fun dropIndex(cell: TreeCell<SceneItemViewModel?>, event: DragEvent): Int
{
	val item = cell.treeItem.value ?: return -1
	val halfHeight = cell.height / 2
	return if (event.y < halfHeight) item.index
	else item.index + 1
}
private fun TreeCell<SceneItemViewModel?>.acceptDragDrop(event: DragEvent)
{
	val halfHeight = height / 2
	if (event.y < halfHeight) style {
		borderColor += box(Styles.Purple, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT)
	}
	else style {
		borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, Styles.Purple, Color.TRANSPARENT)
	}
	event.acceptTransferModes(TransferMode.MOVE)
}