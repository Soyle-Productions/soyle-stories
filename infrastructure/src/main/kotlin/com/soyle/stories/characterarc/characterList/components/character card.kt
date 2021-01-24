package com.soyle.stories.characterarc.characterList.components

import com.soyle.stories.characterarc.Styles.Companion.defaultCharacterImage
import com.soyle.stories.characterarc.characterList.*
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.ComponentsStyles.Companion.liftedCard
import com.soyle.stories.di.resolveLater
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*

class CharacterCard : ItemFragment<CharacterTreeItemViewModel>() {

    private val viewListener by resolveLater<CharacterListViewListener>()

    private val isDisplayingCharacterArcs = SimpleBooleanProperty(false)
    private var nameField by singleAssign<EditableText>()

    private val nameProperty = itemProperty.select { it.name.toProperty() }
    private val imageResourceProperty = itemProperty.select { it.imageResource.toProperty() }
    private val arcs = itemProperty.select { it.arcs.toProperty() }
    private val hasArcs = arcs.select { it.isNotEmpty().toProperty() }

    override val root: VBox = card {}
    init {
        // By separating this out, we can utilize the [hoverProperty()] of [root] in lower elements because root will
        // have been created
        with(root) {
            doNotVerticallyExpandInFlowPane()
            liftCardWhen(isDisplayingCharacterArcs)
            characterCardHeader()
            characterCardBody()
        }
        hasArcs.onChange {
            if (it != true) isDisplayingCharacterArcs.set(false)
        }
    }

    private fun Parent.characterCardHeader(): HBox {
        return cardHeader {
            characterImage()
            characterName()
            spacer()
            characterCardActions()
        }
    }

    private fun Parent.characterImage() {
        val nodeProperty = imageResourceProperty.select<String, Node> {
            if (it.isBlank()) {
                MaterialIconView(defaultCharacterImage, "1.5em").toProperty()
            } else {
                ImageView(it).toProperty()
            }
        }
        nodeProperty.addListener { observable, oldValue, newValue ->
            when {
                oldValue == null -> {
                    getChildList()?.add(0, newValue)
                }
                newValue != null -> oldValue.replaceWith(newValue)
                else -> oldValue?.removeFromParent()
            }
        }
    }

    private fun Parent.characterName(): EditableText {
        return editableText(nameProperty) {
            nameField = this
            root.style { fontSize = 1.25.em }
            onShowing { errorMessage = null }
            setOnAction { _ ->
                val newName = NonBlankString.create(editedText ?: "")
                if (newName == null) {
                    errorMessage = "Character Name cannot be blank"
                    return@setOnAction
                }
                renameCharacter(newName)
                hide()
            }
        }
    }

    private fun renameCharacter(newName: NonBlankString) {
        val characterId = item?.id ?: return
        viewListener.renameCharacter(characterId, newName)
    }

    private fun Parent.characterCardActions(): VBox {
        return vbox(alignment = Pos.CENTER_RIGHT) {
            val optionsButton = characterOptionsButton()
            spacer {
                visibleWhen(hasArcs)
                managedWhen(visibleProperty())
            }
            val displayCharacterArcsButton = displayCharacterArcsButton()
            visibleWhen(
                root.hoverProperty()
                    .or(nameField.focusedProperty())
                    .or(optionsButton.focusedProperty())
                    .or(displayCharacterArcsButton.focusedProperty())
                    .or(isDisplayingCharacterArcs)
            )
        }
    }

    private fun Parent.characterOptionsButton(): MenuButton {
        return buttonCombo {
            graphic = MaterialIconView(MaterialIcon.MORE_VERT)
            this@characterOptionsButton.visibleProperty().onChange { isFocusTraversable = it }
            item("Rename") {
                action { nameField.show() }
            }
            item("New Character Arc") {
                action { planNewCharacterArc() }
            }
            item("Delete") {
                action { deleteCharacter() }
            }
        }
    }

    private fun planNewCharacterArc() {
        val characterId = item?.id ?: return
        planCharacterArcDialog(characterId, currentStage)
    }

    private fun deleteCharacter() {
        val characterItem = item ?: return
        viewListener.removeCharacter(characterItem.id)
    }

    private fun VBox.displayCharacterArcsButton(): Button {
        return button {
            visibleWhen(hasArcs)
            managedWhen(visibleProperty())
            graphic = MaterialIconView(MaterialIcon.EXPAND_MORE)
            isDisplayingCharacterArcs.onChange {
                graphic = if (it) MaterialIconView(MaterialIcon.EXPAND_LESS) else MaterialIconView(MaterialIcon.EXPAND_MORE)
            }
            this@displayCharacterArcsButton.visibleProperty().onChange { isFocusTraversable = it }
            action {
                isDisplayingCharacterArcs.set(!isDisplayingCharacterArcs.get())
            }
        }
    }

    private fun Parent.characterCardBody() = cardBody {
        visibleWhen(isDisplayingCharacterArcs)
        managedWhen(visibleProperty())
        style {
            padding = box(0.px, 16.px, 16.px, 16.px)
        }
        label("Character Arcs") {
            style { fontSize = 1.1.em }
        }
        repeat(arcs.value?.size ?: 0) { i ->
            characterArcItem(this, arcs.select { it.getOrNull(i).toProperty() })
        }
        arcs.addListener { observable, oldValue, newValue ->
            val newSize = newValue?.size ?: 0
            val oldSize = oldValue?.size ?: 0
            if (newSize > oldSize) {
                repeat(newSize - oldSize) { i ->
                    characterArcItem(this, arcs.select { it.getOrNull(i + oldSize).toProperty() })
                }
            }
        }
    }
}

internal fun PopulatedDisplay.characterCard(pane: FlowPane, characterItemProperty: ObservableValue<CharacterTreeItemViewModel>) {
    pane += find<CharacterCard> {
        itemProperty.bind(characterItemProperty)
        characterItemProperty.onChange { if (it == null) this.removeFromParent() }
    }
}

private fun Region.doNotVerticallyExpandInFlowPane() {
    maxHeight = Region.USE_PREF_SIZE
}
private fun Node.liftCardWhen(observablePredicate: ObservableValue<Boolean>) {
    toggleClass(liftedCard, observablePredicate)
}

class CharacterArcItem : ItemFragment<CharacterArcItemViewModel>()
{
    private val viewListener by resolveLater<CharacterListViewListener>()

    private val nameProperty = itemProperty.select { it.name.toProperty() }

    private var nameField by singleAssign<EditableText>()

    override val root: HBox = hbox {}
    init {
        with(root) {
            characterArcName()
            val spacer = spacer {
                managedWhen(visibleProperty())
            }
            val optionsButton = characterArcOptionsButton()
            spacer.visibleWhen(optionsButton.visibleProperty())
        }
    }

    private fun Parent.characterArcName() = editableText(nameProperty) {
        nameField = this
        onShowing { errorMessage = null }
        setOnAction { _ ->
            val newName = editedText ?: ""
            if (newName.isBlank()) {
                errorMessage = "Character Arc Name Cannot Be Blank"
                return@setOnAction
            }
            renameCharacterArc(newName)
            hide()
        }
    }

    private fun renameCharacterArc(newName: String)
    {
        val arcItem = item ?: return
        viewListener.renameCharacterArc(arcItem.characterId, arcItem.themeId, newName)
    }

    private fun Parent.characterArcOptionsButton() = buttonCombo {
        visibleWhen(root.hoverProperty()
            .or(nameField.focusedProperty())
            .or(focusedProperty()))
        managedWhen(visibleProperty())
        graphic = MaterialIconView(MaterialIcon.MORE_VERT)
        item("Rename") {
            action { nameField.show() }
        }
        item("Base Story Structure") {
            action { openBaseStoryStructure() }
        }
        item("Compare Character") {
            action { openCharacterValueComparison() }
        }
        item("Delete") {
            action { deleteCharacterArc() }
        }
    }

    private fun openBaseStoryStructure() {
        val arcItem = item ?: return
        viewListener.openBaseStoryStructureTool(arcItem.characterId, arcItem.themeId)
    }

    private fun openCharacterValueComparison() {
        val arcItem = item ?: return
        viewListener.openCharacterValueComparison(arcItem.themeId)
    }

    private fun deleteCharacterArc() {
        val arcItem = item ?: return
        confirmDeleteCharacterArc(arcItem.characterId, arcItem.themeId, arcItem.name, viewListener)
    }
}

private fun CharacterCard.characterArcItem(parent: Parent, characterArcItemProperty: ObservableValue<CharacterArcItemViewModel>)
{
    parent += find<CharacterArcItem> {
        itemProperty.bind(characterArcItemProperty)
        characterArcItemProperty.onChange { if (it == null) this.removeFromParent() }
    }
}
/*
internal fun PopulatedDisplay.characterCard(characterItem: CharacterTreeItemViewModel): VBox {
    return card {
        maxHeight = Region.USE_PREF_SIZE
        val card = this
        val bodyExpanded = SimpleBooleanProperty(false)
        toggleClass(liftedCard, bodyExpanded)
        cardHeader {
            characterItem.imageResource.takeIf { it.isNotBlank() }?.let {
                imageview(it)
            } ?: this += MaterialIconView(defaultCharacterImage, "1.5em")
            val nameField = editableText(characterItem.name) {
                root.style { fontSize = 1.25.em }
                onShowing { errorMessage = null }
                setOnAction { _ ->
                    val newName = editedText ?: ""
                    try {
                        validateCharacterName(newName)
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "Invalid Character Name"
                        return@setOnAction
                    }
                    characterListViewListener.renameCharacter(characterItem.id, newName)
                    hide()
                }
            }
            spacer()
            vbox(alignment = Pos.CENTER_RIGHT) {
                val buttonContainer = this
                val optionsBtn = buttonCombo {
                    graphic = MaterialIconView(MaterialIcon.MORE_VERT)
                    buttonContainer.visibleProperty().onChange { isFocusTraversable = it }
                    item("Rename") {
                        action { nameField.show() }
                    }
                    item("New Character Arc") {
                        action {
                            planCharacterArcDialog(characterItem.id, currentStage)
                        }
                    }
                    item("Delete") {
                        action {
                            confirmDeleteCharacter(characterItem.id, characterItem.name, characterListViewListener)
                        }
                    }
                }
                val spacer = spacer()
                val expandBtn = button {
                    hiddenWhen { characterItem.arcs.isEmpty().toProperty() }
                    managedProperty().bind(visibleProperty())
                    graphic = MaterialIconView(MaterialIcon.EXPAND_MORE)
                    bodyExpanded.onChange {
                        graphic = if (it) MaterialIconView(MaterialIcon.EXPAND_LESS) else MaterialIconView(MaterialIcon.EXPAND_MORE)
                    }
                    buttonContainer.visibleProperty().onChange { isFocusTraversable = it }
                    action {
                        bodyExpanded.set(!bodyExpanded.get())
                    }
                }
                with(spacer) {
                    managedProperty().bind(expandBtn.managedProperty())
                }
                visibleWhen(
                    card.hoverProperty()
                        .or(nameField.focusedProperty())
                        .or(optionsBtn.focusedProperty())
                        .or(expandBtn.focusedProperty())
                        .or(bodyExpanded)
                )
            }
        }
        cardBody {
            style {
                padding = box(0.px, 16.px, 16.px, 16.px)
            }
            visibleProperty().bind(bodyExpanded)
            managedProperty().bind(visibleProperty())
            label("Character Arcs") {
                style { fontSize = 1.1.em }
            }
            characterItem.arcs.forEach {
                hbox {
                    val arcItem = this
                    val arcNameField = editableText(it.name) {
                        onShowing { errorMessage = null }
                        setOnAction { _ ->
                            val newName = editedText ?: ""
                            if (newName.isBlank()) {
                                errorMessage = "Character Arc Name Cannot Be Blank"
                                return@setOnAction
                            }
                            characterListViewListener.renameCharacterArc(characterItem.id, it.themeId, newName)
                            hide()
                        }
                    }
                    val spacer = spacer()
                    val optionsButton = buttonCombo {
                        visibleWhen(arcItem.hoverProperty()
                            .or(arcNameField.focusedProperty())
                            .or(focusedProperty()))
                        managedProperty().bind(visibleProperty())
                        graphic = MaterialIconView(MaterialIcon.MORE_VERT)
                        item("Rename") {
                            action { arcNameField.show() }
                        }
                        item("Base Story Structure") {
                            action {
                                characterListViewListener.openBaseStoryStructureTool(it.characterId, it.themeId)
                            }
                        }
                        item("Delete") {
                            action {
                                confirmDeleteCharacterArc(it.characterId, it.themeId, it.name, characterListViewListener)
                            }
                        }
                    }
                    spacer.visibleProperty().bind(optionsButton.visibleProperty())
                    spacer.managedProperty().bind(spacer.visibleProperty())
                }
            }
        }
    }
}*/