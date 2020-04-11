/**
 * Created by Brendan
 * Date: 3/11/2020
 * Time: 4:43 PM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.hideScrollbars
import com.soyle.stories.common.launchTask
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.rowCountProperty
import javafx.beans.property.Property
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ContextMenu
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import kotlinx.coroutines.runBlocking
import tornadofx.*
import java.util.logging.Logger

class ComparisonSubTool : Fragment() {

    override val scope = super.scope as CharacterComparisonScope
    val model = find<CharacterComparisonModel>()
    val characterComparisonViewListener: CharacterComparisonViewListener =
        find<CharacterComparisonComponent>().characterComparisonViewListener

    private val compSubTool: Property<CompSubToolViewModel> =
        model.subTools.select { (it.firstOrNull() as? CompSubToolViewModel).toProperty() }
    private val sections = compSubTool.select { it.sections.toProperty() }
    private val sectionCount = sections.select { it?.size.toProperty() }
    private val items = compSubTool.select { it?.items.toProperty() }

    override val root: Parent = form {
        gridpane {
            addClass("table-view")
            fitToParentWidth()
            header()
            items.value?.indices?.forEach {
                addItem(it)
            }
            items.select { it?.size.toProperty() }.addListener { _, i, i2 ->
                val originalSize = i ?: 0
                val newSize = i2 ?: 0
                if (newSize > originalSize) {
                    (originalSize until newSize).forEach { addItem(it) }
                }
            }
        }
    }

    private fun GridPane.header() {
        row()
        pane {
            addClass("column-header")
            gridpaneConstraints {
                columnRowIndex(0, 0)
                fillWidth = true
                hgrow = Priority.NEVER
            }
        }
        label("Story Functions") {
            maxWidth = Double.MAX_VALUE
            minWidth = Region.USE_PREF_SIZE
            alignment = Pos.CENTER
            addClass("column-header")
            gridpaneConstraints {
                columnRowIndex(1, 0)
                fillWidth = true
                hgrow = Priority.NEVER
            }
        }
        val actionsLabel = label("Actions") {
            maxWidth = Double.MAX_VALUE
            minWidth = Region.USE_PREF_SIZE
            alignment = Pos.CENTER
            addClass("column-header")
            gridpaneConstraints {
                columnRowIndex((sectionCount.value ?: 0) + 2, 0)
                fillWidth = true
                hgrow = Priority.NEVER
            }
        }

        fun addSectionLabel(index: Int) {
            val sectionProperty = sections.select { it?.getOrNull(index).toProperty() }
            val label = label {
                alignment = Pos.CENTER
                maxWidth = Double.MAX_VALUE
                minWidth = Region.USE_PREF_SIZE
                addClass("column-header")
                gridpaneConstraints {
                    columnRowIndex(index + 2, 0)
                    fillWidth = true
                    hgrow = Priority.ALWAYS
                }
                textProperty().bind(sectionProperty)
            }
            sectionProperty.onChange {
                if (it == null) {
                    error("Unexpectedly removed header. $index -> ${label.text}, ${sections}")
                    label.removeFromParent()
                } else if (it.isBlank()) {
                    error("Unexpected blank header. $index, ${sections}")
                }
            }
        }
        sections.value?.indices?.forEach(::addSectionLabel)
        sectionCount.addListener { _, i, i2 ->
            val originalSize = i ?: 0
            val newSize = i2 ?: 0
            if (newSize > originalSize) {
                (originalSize until newSize).forEach(::addSectionLabel)
            }
        }
        sectionCount.onChange {
            if (it != null) {
                actionsLabel.gridpaneConstraints {
                    columnIndex = it + 2
                }
            }
        }
    }

    private val storyFunctionMenu = ContextMenu().apply {
        isAutoHide = true
        isAutoFix = true
    }

    private fun GridPane.addItem(index: Int) {
        row {
            val itemProperty = items.select { it?.getOrNull(index).toProperty() }
            val nameLabel = label(itemProperty.stringBinding { it?.characterName ?: "" }) {
                usePrefWidth = true
                gridpaneConstraints {
                    hAlignment = HPos.RIGHT
                    vAlignment = VPos.CENTER
                }
            }
            val isHero = itemProperty.selectBoolean { (it?.storyFunctions?.contains("Hero") == true).toProperty() }
            pane {
                usePrefWidth = true
                maxWidth = Region.USE_PREF_SIZE
                maxHeight = Region.USE_PREF_SIZE
                gridpaneConstraints {
                    vAlignment = VPos.CENTER
                    hAlignment = HPos.CENTER
                }
                fun makeChild(isHero: Boolean) {
                    if (isHero) {
                        label("Hero") {
                            style {
                                fontWeight = FontWeight.BOLD
                            }
                            usePrefWidth = true
                        }
                    } else {
                        button(itemProperty.stringBinding {
                            when (it?.storyFunctions?.size) {
                                1 -> it.storyFunctions.first()
                                0, null -> "[Choose]"
                                else -> it.storyFunctions.toString()
                            }
                        }) {
                            usePrefWidth = true
                            setOnMouseClicked {
                                val storyFunctions = itemProperty.value?.storyFunctions ?: return@setOnMouseClicked
                                if (isHero) return@setOnMouseClicked
                                storyFunctionMenu.apply {
                                    items.clear()
                                    compSubTool.value.storyFunctionOptions.forEach { (label, storyFunction) ->
                                        checkmenuitem(label) {
                                            isSelected = storyFunctions.contains(storyFunction)
                                            action {
                                                val characterId = itemProperty.value?.characterId ?: return@action
                                                launchTask {
                                                    characterComparisonViewListener.setStoryFunction(
                                                            model.focusedCharacter.value.characterId,
                                                            characterId,
                                                            storyFunction
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }.show(this, Side.BOTTOM, 0.0, 0.0)
                            }
                        }
                    }
                }
                isHero.onChange {
                    children.clear()
                    makeChild(it)
                }
                makeChild(isHero.value == true)
            }
            sections.value?.indices?.forEach {
                addItemSection(itemProperty, it)
            }
            val actions = hbox(5) {
                usePrefHeight = true
                maxHeight = Region.USE_PREF_SIZE
                gridpaneConstraints {
                    vAlignment = VPos.CENTER
                }
                hiddenWhen { isHero }
                managedProperty().bind(visibleProperty())
                button(itemProperty.stringBinding {
                    if (it?.isMajorCharacter == true) "Demote"
                    else "Promote"
                }) {
                    usePrefWidth = true
                    action {
                        isDisable = true
                        val item = itemProperty.value ?: return@action
                        runAsync {
                            runBlocking {
                                if (item.isMajorCharacter) {
                                    characterComparisonViewListener.demoteCharacter(
                                        item.characterId
                                    )
                                } else {
                                    characterComparisonViewListener.promoteCharacter(
                                        item.characterId
                                    )
                                }
                            }
                        } ui {
                            isDisable = false
                        }
                    }
                }
                button("Remove") {
                    usePrefWidth = true
                    action {
                        launchTask {
                            characterComparisonViewListener.removeCharacterFromComparison(
                                itemProperty.value.characterId
                            )
                        }
                    }
                }
            }
            sectionCount.addListener { _, i, i2 ->
                if (i2 > i) {
                    (i..i2).forEach { addItemSection(itemProperty, it) }
                    actions.gridpaneConstraints { columnIndex = i2 + 2 }
                }
            }
            itemProperty.onChangeUntil({ it == null }) {
                if (it == null) {
                    Logger.getGlobal().warning("Item is now null, removing row: ${nameLabel.text}")
                    removeRow(nameLabel)
                }
            }
        }
    }

    private fun Node.addItemSection(itemProperty: Property<ComparisonItem>, index: Int) {
        val sectionValueProperty = itemProperty.objectBinding(sections) { item ->
            val section = sections.value?.get(index)
            val value = item?.compSections?.get(section)
            value
        }
        val inputContainer = pane {
            addClass("table-cell")
            GridPane.setHgrow(this, Priority.ALWAYS)
            maxWidth = Region.USE_COMPUTED_SIZE
        }
        val input = inputContainer.textarea {
            paddingAll = 0.0
            isWrapText = true
            minWidth = Region.USE_COMPUTED_SIZE
            fitToParentWidth()
            hideScrollbars()
            prefRowCountProperty().bind(rowCountProperty)

            text = sectionValueProperty.value?.value ?: ""
            sectionValueProperty.onChange { text = it?.value ?: "" }
            focusedProperty().onChange {
                val sectionValue = sectionValueProperty.value ?: return@onChange
                if (!it && sectionValue.value != text) {
                    isDisable = true
                    launchTask {
                        when (sectionValue) {
                            is PropertyValue -> if (sectionValue.isShared) {
                                characterComparisonViewListener.changeSharedPropertyValue(
                                    model.focusedCharacter.value.characterId,
                                    itemProperty.value.characterId,
                                    sectionValue.propertyName,
                                    text
                                )
                            } else {
                                characterComparisonViewListener.changeCharacterPropertyValue(
                                    itemProperty.value.characterId,
                                    sectionValue.propertyName,
                                    text
                                )
                            }
                            is CharacterArcSectionValue -> characterComparisonViewListener.updateValue(
                                sectionValue.sectionId,
                                text
                            )
                        }
                    } ui {
                        isDisable = false
                    }
                }
            }
        }
        sectionValueProperty.onChangeUntil({ it == null }) {
            if (it == null) input.removeFromParent()
        }
    }

    class Styles : Stylesheet() {
        companion object {
            val tableColumnHeader by cssclass()
            val table by cssclass()

            init {
                importStylesheet(Styles::class)
            }
        }

        init {
            Companion.table {
                borderWidth += box(1.0.px)
                borderColor += box(Color.web("#bbbbbb"))
            }
            tableColumnHeader {
                unsafe("-fx-background-color", raw("-fx-body-color"))
                padding = box(0.166667.em, 0.833333.em, 0.25.em, 0.833333.em)
                unsafe("-fx-text-fill", raw("-fx-text-base-color"))
                alignment = Pos.CENTER
                contentDisplay = ContentDisplay.LEFT

                borderStyle += BorderStrokeStyle.SOLID
                fontWeight = FontWeight.BOLD
                borderColor = multi(
                    box(
                        Color.rgb(0xd0, 0xd0, 0xd0).derive(0.80),
                        Color.rgb(0xd0, 0xd0, 0xd0).derive(0.80),
                        Color.rgb(0xd0, 0xd0, 0xd0).derive(0.10),
                        Color.rgb(0xd0, 0xd0, 0xd0).derive(0.80)
                    ),
                    box(
                        Color.TRANSPARENT, Color.GREY, Color.GREY, Color.TRANSPARENT
                    )
                )
                borderInsets = multi(box(0.px, 1.px, 1.px, 0.px), box(0.px, 0.px, 0.px, 0.px))
                borderWidth = multi(box(0.083333.em), box(0.083333.em))
            }
        }
    }
}