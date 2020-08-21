package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.associateChildrenTo
import com.soyle.stories.common.components.responsiveBox
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.characterConflict.components.*
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextInputControl
import javafx.scene.control.ToggleButton
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

class CharacterConflict : View() {

    private val viewListener = resolve<CharacterConflictViewListener>()
    private val model = resolve<CharacterConflictModel>()

    private val perspectiveCharacterSelected get() = model.selectedPerspectiveCharacter.isNotNull

    private val selectedContentTab = SimpleStringProperty("").apply {
        cleanBind(model.characterChangeLabel)
    }

    override val root = form {
        vbox(spacing = 8.0) {
            thematicSection()
            characterContent()
        }
    }

    private fun Parent.thematicSection(): Node {
        return fieldset(labelPosition = Orientation.VERTICAL) {
            responsiveBox(model.isSmall, hSpacing = 8.0, vSpacing = 8.0) {
                centralConflictField().apply {
                    hgrow = Priority.ALWAYS
                }
                perspectiveCharacterSelectionField().apply {
                    hgrow = Priority.NEVER
                }
            }
        }
    }

    private fun Parent.centralConflictField(): Field {
        return field {
            textProperty.bind(model.centralConflictFieldLabel)
            centralConflictInput().apply {
                hgrow = Priority.ALWAYS
            }

        }
    }

    private fun Parent.centralConflictInput(): Node {
        return textfield(model.centralConflict) {
            onLoseFocus { viewListener.setCentralConflict(text) }
        }
    }

    private fun Parent.perspectiveCharacterSelectionField(): Field {
        return field {
            textProperty.bind(model.perspectiveCharacterLabel)
            perspectiveCharacterSelection().apply {
                this.hgrow = Priority.ALWAYS
                this.maxWidth = Double.MAX_VALUE
            }
        }
    }

    private fun Parent.perspectiveCharacterSelection(): MenuButton {
        return menubutton {
            textProperty().bind(model.selectedPerspectiveCharacter.select { it?.characterName.toProperty() })
            setOnShowing { viewListener.getAvailableCharacters() }
            setOnHidden { model.availablePerspectiveCharacters.value = null }
            populatePerspectiveCharacterList(model)
            onCreateCharacter = {
                createCharacterDialog(
                    model.scope.projectScope,
                    model.scope.themeId,
                    true
                )
            }
            onPerspectiveCharacterSelected = {
                model.selectedPerspectiveCharacter.value = CharacterItemViewModel(it.characterId, it.characterName, "")
                viewListener.getValidState(it.characterId)
            }
        }
    }

    private fun Parent.characterContent() {
        contentTabs()
        scrollpane {
            existsWhen { perspectiveCharacterSelected }
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            isFitToWidth = true
            addClass("edge-to-edge")
            content = vbox {
                characterChangeSection()
                opponentsSection()
            }
        }
    }

    private fun Parent.contentTabs(): Node {
        return hbox {
            existsWhen { model.isSmall }
            togglegroup {
                contentTab(model.characterChangeLabel).apply {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                }
                contentTab(model.opponentSectionsLabel).apply {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                }
            }
        }
    }

    private fun Parent.contentTab(labelProperty: ObservableValue<String>): ToggleButton {
        return togglebutton(labelProperty) {
            action {
                if (!isSelected) isSelected = true
                selectedContentTab.cleanBind(labelProperty)
            }
        }
    }

    private fun Parent.characterChangeSection() {
        fieldset(labelPosition = Orientation.VERTICAL) {
            existsWhen {
                val characterChangeLabel = model.characterChangeLabel as SimpleStringProperty
                val characterChangeTabSelected = selectedContentTab.isEqualTo(characterChangeLabel)
                val shouldExist = model.isLarge.or(characterChangeTabSelected)
                shouldExist
            }
            characterChangeSectionTitle()
            characterChangeFields()
        }
    }

    private fun Parent.characterChangeSectionTitle(): Node {
        return sectionTitle(model.characterChangeLabel).apply {
            existsWhen { model.isLarge }
        }
    }

    private fun Parent.characterChangeFields(): Node {
        return responsiveBox(model.isSmall, hSpacing = 8.0, vSpacing = 8.0) {
            listOf(
                characterChangeField(model.desireLabel, model.desire, ::setDesire),
                characterChangeField(model.psychologicalWeaknessLabel, model.psychologicalWeakness, ::setPsychologicalWeakness),
                characterChangeField(model.moralWeaknessLabel, model.moralWeakness, ::setMoralWeakness),
                characterChangeField(model.characterChangeLabel, model.characterChange, ::setCharacterChange)
            ).onEach {
                it.hgrow = Priority.ALWAYS
            }
        }
    }

    private fun setDesire(desire: String)
    {
        val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return
        viewListener.setDesire(perspectiveCharacterId, desire)
    }

    private fun setPsychologicalWeakness(weakness: String)
    {
        val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return
        viewListener.setPsychologicalWeakness(perspectiveCharacterId, weakness)
    }

    private fun setMoralWeakness(weakness: String)
    {
        val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return
        viewListener.setMoralWeakness(perspectiveCharacterId, weakness)
    }

    private fun setCharacterChange(characterChange: String)
    {
        val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return
        viewListener.setCharacterChange(perspectiveCharacterId, characterChange)
    }

    private fun Parent.characterChangeField(
        labelProperty: ObservableValue<String>,
        valueProperty: ObservableValue<String>,
        setCharacterChangeField: (String) -> Unit
    ): Field {
        return field {
            textProperty.bind(labelProperty)
            characterChangeInput(valueProperty).apply {
                hgrow = Priority.ALWAYS
                onLoseFocus {
                    setCharacterChangeField(text)
                }
            }
        }
    }

    private fun Parent.characterChangeInput(valueProperty: ObservableValue<String>): TextInputControl {
        return textfield(valueProperty)
    }

    private fun Parent.opponentsSection() {
        fieldset {
            existsWhen { model.isLarge.or(selectedContentTab.isEqualTo(model.opponentSectionsLabel as SimpleStringProperty)) }
            vbox {
                opponentSectionHeader()
                opponentPropertyColumnHeaders()
                opponents()
            }
        }
    }

    private fun Parent.opponentSectionHeader(): Node {
        return hbox {
            opponentsSectionTitle()
            spacer()
            addOpponentSelection()
        }
    }

    private fun Parent.opponentsSectionTitle(): Node {
        return sectionTitle(model.opponentSectionsLabel).apply {
            existsWhen { model.isLarge }
        }
    }

    private fun Parent.sectionTitle(textProperty: ObservableValue<String>): Node {
        return label(textProperty) {
            style {
                fontSize = 1.25.em
            }
        }
    }

    private fun Parent.addOpponentSelection() {
        menubutton("Add Opponent") {
            existsWhen {
                model.isLarge.or(
                    selectedContentTab.isEqualTo(
                        model.opponentSectionsLabel as SimpleStringProperty
                    )
                )
            }
            setOnShowing {
                val perspectiveCharacterId =
                    model.selectedPerspectiveCharacter.value?.characterId ?: return@setOnShowing
                viewListener.getAvailableOpponents(perspectiveCharacterId)
            }
            setOnHidden { model.availableOpponents.value = null }
            populateOpponentList(model)
            onCreateCharacter = onCreateCharacter@{
                val perspectiveCharacterId =
                    model.selectedPerspectiveCharacter.value?.characterId ?: return@onCreateCharacter
                createCharacterDialog(
                    model.scope.projectScope,
                    model.scope.themeId,
                    useAsOpponentForCharacter = perspectiveCharacterId
                )
            }
            onOpponentCharacterSelected = onOpponentCharacterSelected@{
                val perspectiveCharacter =
                    model.selectedPerspectiveCharacter.value
                        ?: return@onOpponentCharacterSelected
                viewListener.addOpponent(
                    perspectiveCharacter.characterId,
                    it.characterId
                )
            }
        }
    }

    private fun Parent.opponentPropertyColumnHeaders() {
        val opponentPropertyLabels = listOf(
            model.attackSectionLabel,
            model.similaritiesSectionLabel,
            model.powerStatusOrAbilitiesLabel
        )
        hbox(spacing = 8.0, alignment = Pos.BOTTOM_CENTER) {
            paddingHorizontal = 32.0
            existsWhen { model.isLarge }
            opponentPropertyLabels.forEach {
                hbox(alignment = Pos.BOTTOM_CENTER) {
                    hgrow = Priority.ALWAYS
                    prefWidth = 0.0
                    minWidth = 0.0
                    label(it) {
                        style {
                            fontWeight = FontWeight.BOLD
                        }
                    }
                }
            }
        }
        menubutton {
            existsWhen { model.isSmall.and(selectedContentTab.isEqualTo(model.opponentSectionsLabel as SimpleStringProperty)) }
            textProperty().bind(model.selectedOpponentPropertyColumn)
            opponentPropertyLabels.forEach {
                item("") {
                    textProperty().bind(it)
                    action { model.selectedOpponentPropertyColumn.cleanBind(it) }
                }
            }
        }

    }

    private fun Parent.opponents() {
        mainOpponentSection()
        minorOpponentsSection()
    }

    private fun Parent.mainOpponentSection() {
        mainOpponentSectionTitle()
        opponentCard(model.mainOpponent as SimpleObjectProperty)
    }

    private fun Parent.mainOpponentSectionTitle() {
        sectionTitle("Main Opponent".toProperty())
    }

    private fun Parent.opponentCard(opponentModel: ObservableObjectValue<CharacterChangeOpponentViewModel?>): Node {
        val card = find<OpponentCard> {
            itemProperty.bind(opponentModel)
            onOpponentSelectedToBeMain = listener@{
                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return@listener
                viewListener.makeOpponentMainOpponent(perspectiveCharacterId, it)
            }
            onRemoveOpponent = listener@{
                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return@listener
                viewListener.removeOpponent(perspectiveCharacterId, it)
            }
            opponentAttack.onChange {
                if (it == null) return@onChange
                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return@onChange
                val opponentId = opponentModel.value?.characterId ?: return@onChange
                viewListener.setAttackFromOpponent(perspectiveCharacterId, opponentId, it)
            }
            opponentSimilarities.onChange {
                if (it == null) return@onChange
                val perspectiveCharacterId = model.selectedPerspectiveCharacter.value?.characterId ?: return@onChange
                val opponentId = opponentModel.value?.characterId ?: return@onChange
                viewListener.setCharactersSimilarities(perspectiveCharacterId, opponentId, it)
            }
            opponentAbility.onChange {
                if (it == null) return@onChange
                val opponentId = opponentModel.value?.characterId ?: return@onChange
                viewListener.setCharacterAbilities(opponentId, it)
            }
        }
        add(card)
        return card.root
    }

    private fun Parent.minorOpponentsSection() {
        minorOpponentsSectionTitle()
        minorOpponentList()
    }

    private fun Parent.minorOpponentsSectionTitle()
    {
        sectionTitle("Minor Opponents".toProperty())
    }

    private fun Parent.minorOpponentList()
    {
        vbox(spacing = 8.0) {
            val opponentsById = model.opponents.select {
                it.associateBy { it.characterId }.toProperty()
            }
            associateChildrenTo(opponentsById) {
                opponentCard(it as SimpleObjectProperty)
            }
        }
    }

    init {
        model.invalidatedProperty().onChange {
            getValidStateIfInvalid(it, null)
        }
        root.widthProperty().onChange {
            if (it < 600) model.isSmall.set(true)
            else model.isSmall.set(false)
        }
        getValidStateIfInvalid(model.invalidated, model.scope.type.characterId?.toString())
    }

    private fun getValidStateIfInvalid(invalidated: Boolean, characterId: String?) {
        if (invalidated) viewListener.getValidState(characterId)
    }

}