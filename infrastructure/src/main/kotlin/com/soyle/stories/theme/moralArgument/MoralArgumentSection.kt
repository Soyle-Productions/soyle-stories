package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.dragHandle.DragHandleImpl.Companion.dragHandle
import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.di.resolveLater
import com.soyle.stories.theme.moralArgument.MoralArgumentInsertionPoint.Companion.insertionPoint
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*
import com.soyle.stories.common.components.Styles as ComponentStyles

class MoralArgumentSection : Fragment() {

    companion object {
        fun Parent.moralArgumentSection(
            scope: Scope,
            viewModel: MoralArgumentSectionViewModel,
            removeSectionLabel: ObservableValue<String>,
            insertionIndex: ObservableValue<Number>,
        ): MoralArgumentSection {
            val props = mapOf(
                MoralArgumentSection::viewModel to viewModel,
                MoralArgumentSection::insertionIndex to insertionIndex,
                MoralArgumentSection::removeSectionLabel to removeSectionLabel
            )
            val moralArgumentSection = find<MoralArgumentSection>(scope, params = props)
            this += moralArgumentSection
            return moralArgumentSection
        }

        private val viewModelDragDataFormat = DataFormat("raw/MoralArgumentSectionViewModel")
    }

    private val viewModel: MoralArgumentSectionViewModel by param()
    private val insertionIndex: ObservableValue<Number> by param()
    private val removeSectionLabel: ObservableValue<String> by param()

    private val viewListener: MoralArgumentViewListener by resolveLater()

    var onDragging: () -> Unit = {}
    var onDragStop: () -> Unit = {}
    var onSectionPlacedAbove: (String) -> Unit = {}
    var onSectionPlacedBelow: (String) -> Unit = {}
    var onSectionDraggedAbove: (String) -> Unit = {}
    var onSectionDraggedBelow: (String) -> Unit = {}
    var onSectionDraggedAway: () -> Unit = {}
    var onMoved: () -> Unit = {}

    override val root: Region = vbox {
        addClass(Styles.moralArgumentSection)
        setOnDragOver(this@MoralArgumentSection::onDragOver)
        setOnDragExited(this@MoralArgumentSection::onDragExit)
        setOnDragDropped(this@MoralArgumentSection::onDragDrop)
        setOnDragDone(this@MoralArgumentSection::onDragDone)
    }

    private val insertionPoint = root.insertionPoint(
        scope,
        viewModel,
        tryingToInsertProperty = insertionIndex.booleanBinding { root.indexInParent != -1 && it == root.indexInParent }
    )
    private val sectionField = root.vbox {
        id = viewModel.arcSectionId
        addClass(ComponentStyles.labeledSection)
        hbox {
            fieldLabel(viewModel.arcSectionName)
            if (viewModel.canBeRemoved) {
                spacer()
                button(removeSectionLabel) {
                    addClass("remove-button")
                    action {
                        viewListener.removeSection(viewModel.arcSectionId)
                    }
                }
            }
        }
        hbox {
            spacing = 8.0
            paddingLeft = -24
            dragHandle {
                targetNode = root
                transferModes = listOf(TransferMode.MOVE)
                clipboardContent = {
                    put(viewModelDragDataFormat, viewModel.arcSectionId)
                }
                onDragDetected = ::onDragStart

                onMoveUp = ::onMoveUp
                onMoveDown = ::onMoveDown
                onMoveDone = ::onMoveDone
                onMoveCancelled = ::onMoveCancelled
            }
            textfield(viewModel.arcSectionValue)
        }
        paddingLeft = 32
    }

    private var originalIndexMovedFrom: Int? = null

    private fun onMoveUp() {
        val parent = root.parent ?: return
        val indexInParent = root.indexInParent
        if (indexInParent <= 0) return
        if (originalIndexMovedFrom == null) originalIndexMovedFrom = indexInParent
        parent.getChildList()?.swap(indexInParent, indexInParent - 1)
    }
    private fun onMoveDown() {
        val parent = root.parent ?: return
        val indexInParent = root.indexInParent
        if (indexInParent == -1 || indexInParent >= parent.childrenUnmodifiable.size - 1) return
        if (originalIndexMovedFrom == null) originalIndexMovedFrom = indexInParent
        parent.getChildList()?.swap(indexInParent, indexInParent + 1)
    }

    private fun onMoveCancelled() {
        val parent = root.parent ?: return
        val indexInParent = root.indexInParent
        originalIndexMovedFrom?.let {
            parent.getChildList()?.run {
                add(it, removeAt(indexInParent))
            }
        }
        originalIndexMovedFrom = null
    }

    private fun onMoveDone() {
        root.parent ?: return
        val indexInParent = root.indexInParent
        if (originalIndexMovedFrom != null && indexInParent != originalIndexMovedFrom) onMoved()
        originalIndexMovedFrom = null
    }

    private fun onDragStart() {
        root.isVisible = false
        onDragging()
    }

    private fun onDragOver(event: DragEvent) {
        val (source, sourceVM) = event.getValidSourceNode() ?: return
        event.consume()
        event.acceptTransferModes(TransferMode.MOVE)
        when (event.determineDropPlacement()) {
            DropPlacement.Top -> onSectionDraggedAbove(sourceVM)
            DropPlacement.Bottom -> onSectionDraggedBelow(sourceVM)
            else -> {}
        }
    }

    private fun onDragExit(event: DragEvent) {
        root.style = ""
        onDragStop()
    }

    private fun onDragDrop(event: DragEvent) {
        val (source, sourceVM) = event.getValidSourceNode() ?: return
        event.consume()
        event.isDropCompleted = true
        when (event.determineDropPlacement()) {
            DropPlacement.Top -> onSectionPlacedAbove(sourceVM)
            DropPlacement.Bottom -> onSectionPlacedBelow(sourceVM)
            else -> onSectionDraggedAway()
        }
    }

    private fun onDragDone(event: DragEvent) {
        root.isVisible = true
    }

    private fun DragEvent.getValidSourceNode(): Pair<Node, String>? {
        val sourceNode = (gestureSource as? Node)
            ?.takeIf { it != root }
            ?: return null
        val content = dragboard.getContent(viewModelDragDataFormat) as? String
            ?: return null
        return sourceNode to content
    }

    private enum class DropPlacement {
        Top, Bottom, None
    }
    private fun DragEvent.determineDropPlacement(): DropPlacement {
        /*
        y is relative to the root, but the top/bottom determination is based on the position relative to the field.
        So, first we have to subtract the height of the insertion point.
         */

        val yRelativeToField = y - insertionPoint.height

        return when {
            yRelativeToField > sectionField.height / 2 -> DropPlacement.Bottom
            else -> DropPlacement.Top
        }
    }

    class Styles: Stylesheet() {
        companion object {

            val moralArgumentSection by cssclass()
            val dragTop by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            moralArgumentSection {
                borderWidth = multi(box(4.px))
                borderColor = multi(box(Color.TRANSPARENT))
            }
        }

    }
}