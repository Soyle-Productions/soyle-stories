package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.common.components.Chip
import com.soyle.stories.common.components.chip
import com.soyle.stories.common.components.menuChipGroup.menu
import com.soyle.stories.common.components.menuChipGroup.menuchipgroup
import com.soyle.stories.di.get
import com.soyle.stories.scene.sceneDetails.SceneDetailsStyles
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import tornadofx.*
import kotlin.math.max

class PositionOnArcSelection(
    parent: Parent,
    private val state: IncludedCharacterInSceneState,
    private val viewListener: IncludedCharacterInSceneViewListener
) {

    private val selection = observableMapOf<String, String>()

    private val menuChipGroup = parent.menuchipgroup {
        noSelectionText = "Nothing Selected"

        onShowing { getUpToDateAvailability() }
        whenAvailabilityIsUpdated { populateMenu(it) }
        onHidden {
            updateCoveredSectionsIfDifferent()
            clearAvailability()
        }
    }

    init {
        initializeChipsWithCoveredArcSections()
        populateMenu(state.availableCharacterArcSections.value)
    }

    private val items get() = menuChipGroup.items
    private val chips get() = menuChipGroup.chips

    private fun whenAvailabilityIsUpdated(op: (ObservableList<AvailableCharacterArcViewModel>?) -> Unit)
    {
        val availabilityListener = ChangeListener<ObservableList<AvailableCharacterArcViewModel>?> { _, _, newValue ->
            op(newValue)
        }
        state.scope.isClosedProperty().onChangeOnce {
            state.availableCharacterArcSections.removeListener(availabilityListener)
        }
        state.availableCharacterArcSections.addListener(availabilityListener)
    }

    private fun populateMenu(availability: ObservableList<AvailableCharacterArcViewModel>?)
    {
        if (availability == null) displayLoadingItem()
        else {
            displayCreateCharacterArcItem()
            availability.forEach { displayCharacterArcItem(it) }
        }
    }

    private fun displayLoadingItem() {
        items.setAll(MenuItem("Loading ...") .apply {
            isDisable = true
        })
    }

    private fun displayCreateCharacterArcItem() {
        items.setAll(MenuItem("Create Character Arc"))
    }

    private fun displayCharacterArcItem(arc: AvailableCharacterArcViewModel)
    {
        // add menu to menuChipGroup with a counter graphic and the arc name as the label
        menuChipGroup.menu(arc.characterArcName) {
            id = arc.characterArcId
            userData = arc
            graphic = counterGraphic(arc)
            item("Create Character Arc Section") {
                action {
                    state.scope.projectScope.get<CreateArcSectionDialogView>().show(state.characterId.valueSafe, arc.themeId, state.scope.sceneId)
                }
            }
            arc.sections.forEach {
                displayArcSectionItem(it)
            }
        }
    }

    private fun counterGraphic(arc: AvailableCharacterArcViewModel): Node
    {
        val totalSectionCount = arc.sections.size
        val incrementer = SimpleIntegerProperty(arc.numberOfCoveredSections)
        val graphic = Label().apply {
            addClass(SceneDetailsStyles.arcSelectionCounter)
            textProperty().bind(stringBinding(incrementer) { value?.toString() ?: "0" })
            incrementer.onChange {
                styleClass.clear()
                addClass(SceneDetailsStyles.arcSelectionCounter)
                when {
                    it == 0 -> addClass(SceneDetailsStyles.noneSelected)
                    it < totalSectionCount -> addClass(SceneDetailsStyles.someSelected)
                    else -> addClass(SceneDetailsStyles.allSelected)
                }
            }
            when {
                arc.allSectionsCovered -> addClass(SceneDetailsStyles.allSelected)
                arc.numberOfCoveredSections == 0 -> addClass(SceneDetailsStyles.noneSelected)
                arc.numberOfCoveredSections < totalSectionCount -> addClass(SceneDetailsStyles.someSelected)
            }
        }
        if (menuChipGroup.isShowing) {
            val allSectionIds = arc.sections.map { it.arcSectionId }.toSet()
            val selectionListener = MapChangeListener<String, String> {
                if (it.key in allSectionIds) {
                    if (it.wasAdded()) incrementer.set(incrementer.get() + 1)
                    else if (it.wasRemoved()) incrementer.set(incrementer.get() - 1)
                }
            }
            menuChipGroup.showingProperty().onChangeOnce { selection.removeListener(selectionListener) }
            selection.addListener(selectionListener)
        }
        return graphic
    }

    private fun Menu.displayArcSectionItem(arcSection: AvailableArcSectionViewModel)
    {
        customitem(hideOnClick = false) {
            addClass(SceneDetailsStyles.arcSectionItem)
            id = arcSection.arcSectionId
            userData = arcSection
            val checkBox = CheckBox().apply {
                fillWidth(parentMenuProperty())
                text = arcSection.arcSectionLabel
                isSelected = arcSection.isCovered
                val selectionListener = MapChangeListener<String, String> { change ->
                    if (change.wasAdded() && change.key == arcSection.arcSectionId) isSelected = true
                    else if (change.wasRemoved() && change.key == arcSection.arcSectionId) isSelected = false
                }
                if (menuChipGroup.isShowing) {
                    menuChipGroup.showingProperty().onChangeOnce {
                        selection.removeListener(selectionListener)
                    }
                    selection.addListener(selectionListener)
                }
                selectedProperty().onChange { nowSelected ->
                    if (nowSelected && arcSection.arcSectionId !in selection) {
                        selection[arcSection.arcSectionId] = arcSection.labelWhenSelected
                    }
                    else if (! nowSelected && arcSection.arcSectionId in selection) {
                        selection.remove(arcSection.arcSectionId)
                    }
                }
            }
            setOnAction {
                checkBox.isSelected = ! checkBox.isSelected
            }
            content = checkBox
        }
    }

    private val Menu.maxCheckBoxWidth: SimpleDoubleProperty
        get() = properties.getOrPut("maxCheckBoxWidth") { SimpleDoubleProperty(0.0) } as SimpleDoubleProperty

    private fun CheckBox.fillWidth(parentMenuProperty: ObservableValue<Menu?>)
    {
        fun updatePrefWidthLater(parentMenu: Menu) {
            Platform.runLater {
                prefWidth = parentMenu.maxCheckBoxWidth.value
            }
        }
        fun updateWidthWhenParentMenuIsShowing(parentMenu: Menu) {
            widthProperty().onChange {
                parentMenu.maxCheckBoxWidth.set(max(it, parentMenu.maxCheckBoxWidth.get()))
            }
            if (parentMenu.isShowing) {
                updatePrefWidthLater(parentMenu)
            } else {
                parentMenu.showingProperty().onChangeOnce {
                    updatePrefWidthLater(parentMenu)
                }
            }
        }
        val parentMenu = parentMenuProperty.value
        if (parentMenu != null) updateWidthWhenParentMenuIsShowing(parentMenu)
        else parentMenuProperty.onChangeOnce {
            if (it != null) updateWidthWhenParentMenuIsShowing(it)
        }
    }


    private fun getUpToDateAvailability()
    {
        state.availableCharacterArcSections.value = null
        viewListener.getAvailableCharacterArcSections()
    }

    private fun initializeChipsWithCoveredArcSections() {
        /*
        this bind call expects the value returned by the lambda to be the exact same value, but we're creating a new chip
        each time, so old chips aren't being removed.
         */
        val chipCache = mutableMapOf<String, Chip>()
        chips.bind(selection) { arcSectionId, displayLabel ->
            chipCache.getOrPut(arcSectionId) {
                menuChipGroup.chip {
                    node.userData = arcSectionId
                    text = displayLabel
                    onDelete {
                        // prevent clicking on delete buttons from triggering menu opening/closing
                        it.consume()
                        // if the menu is showing, we perform a bulk update when it closes, but, if it isn't open, this
                        // one selected item can be removed.
                        if (menuChipGroup.isShowing) {
                            selection.remove(arcSectionId)
                        } else {
                            viewListener.coverCharacterArcSectionInScene(
                                emptyList(),
                                listOf(arcSectionId)
                            )
                        }
                    }
                }
            }
        }

        selection.putAll(state.coveredArcSections.associate { it.arcSectionId to it.displayLabel })
        state.coveredArcSections.onChange<ObservableList<CoveredArcSectionViewModel>?> {
            selection.clear()
            // removes old chips that were no longer selected.
            chipCache.clear()
            selection.putAll(state.coveredArcSections.associate { it.arcSectionId to it.displayLabel })
        }
    }

    private fun updateCoveredSectionsIfDifferent()
    {
        val selectedIds = selection.keys.toSet()
        val previouslyCoveredSections = state.coveredArcSections
            .map { it.arcSectionId }
            .toSet()
        val newlySelectedIds = selectedIds - previouslyCoveredSections
        val deselectedIds = previouslyCoveredSections - selectedIds

        if (newlySelectedIds.isNotEmpty() || deselectedIds.isNotEmpty()) {
            println("covering sections.. ${newlySelectedIds}")
            viewListener.coverCharacterArcSectionInScene(
                newlySelectedIds.toList(),
                deselectedIds.toList()
            )
        }
    }

    private fun clearAvailability() {
        items.clear()
        state.availableCharacterArcSections.value = null
    }

}