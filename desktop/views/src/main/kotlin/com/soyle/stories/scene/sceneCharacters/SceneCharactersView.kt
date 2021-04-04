package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyContextMenu
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.notFirstChild
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.buttons.primaryMenuButton
import com.soyle.stories.common.components.dataDisplay.list.ListStyles
import com.soyle.stories.common.components.dataDisplay.list.ListStyles.Companion.applyFirstChildPseudoClasses
import com.soyle.stories.common.components.dataDisplay.list.ListStyles.Companion.removeListViewBorder
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.SceneStyles
import com.soyle.stories.scene.sceneCharacters.SceneCharactersInviteImageView.Companion.sceneCharactersInviteImage
import com.soyle.stories.scene.sceneCharacters.characterEditor.SelectedSceneCharacterEditor.Companion.selectedSceneCharacterEditor
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView.Companion.includedCharacterItem
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemModel
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListCell
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.fxmisc.richtext.TextExt
import tornadofx.*

class SceneCharactersView : View() {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val props = resolve<SceneCharactersState>()
    private val logic = SceneCharactersViewLogic(this, props)

    override val root = vbox {
        addClass(Styles.sceneCharacters)
        selectedSceneHeader().apply { viewOrder = 0.0 }

        stackpane {
            viewOrder = 1.0
            vgrow = Priority.ALWAYS
            noSceneSelected()
            selectedSceneCharacterList()
            selectedSceneCharacterEditor(scope, props.characterBeingEdited)
        }
    }

    @ViewBuilder
    private fun Parent.selectedSceneHeader(): Node {
        return hbox {
            addClass(SceneStyles.selectedSceneHeader)
            controlledBy(logic::selectedSceneHeader)
            toolTitle("Scene: ")
            selectedSceneName()
        }
    }

    @ViewBuilder
    private fun Parent.selectedSceneName() {
        toolTitle(props.selectedSceneName)
    }

    @ViewBuilder
    private fun StackPane.noSceneSelected() {
        invitation {
            existsWhen(props.isSceneSelected.not())
            noSceneSelectedMessage()
        }
    }

    @ViewBuilder
    private fun Parent.invitation(message: @ViewBuilder Parent.() -> Unit): Node {
        return emptyToolInvitation {
            sceneCharactersInviteImage()
            toolTitle(props.sceneCharactersTitleText)
            message()
        }
    }

    @ViewBuilder
    private fun Parent.noSceneSelectedMessage() {
        textflow {
            textAlignment = TextAlignment.CENTER
            text("No scene has been selected to track characters.  Click on a scene in the ")
            hyperlink("Scene List") {
                controlledBy(logic::sceneListHyperLink)
                style { fontWeight = FontWeight.BOLD }
            }
            text(" or click anywhere inside of an open Scene Editor to ")
            label("select") { addClass(TextStyles.warning) }
            text(" a scene and see what characters are in the scene")
        }
    }

    @ViewBuilder
    private fun StackPane.selectedSceneCharacterList() {
        stackpane {
            existsWhen(props.isSceneSelected)
            noIncludedCharacters()
            charactersIncluded()
        }
    }

    @ViewBuilder
    private fun StackPane.noIncludedCharacters() {
        invitation {
            existsWhen(props.hasIncludedCharacters.not())
            noIncludedCharactersMessage()
            addCharacterButton().apply { addClass(ButtonStyles.inviteButton) }
        }
    }

    @ViewBuilder
    private fun Parent.noIncludedCharactersMessage() {
        textflow {
            textAlignment = TextAlignment.CENTER
            text("When you ")
            label("@mention") { addClass(TextStyles.mention) }
            text(" " +
                """
                    a character in the scene, you can choose to add the character to the scene.  However, 
                    you can also choose to add a character to this scene by clicking the button below.
                """.trimIndent().filterNot { it == '\n' })

        }
    }

    @ViewBuilder
    private fun StackPane.charactersIncluded() {
        val selectedCharacter = SimpleObjectProperty<IncludedCharacterViewModel?>()
        vbox {
            existsWhen(props.hasIncludedCharacters)
            includedCharacterListHeader(selectedCharacter)
            includedCharacterList(selectedCharacter).apply { vgrow = Priority.ALWAYS }
        }
    }

    @ViewBuilder
    private fun VBox.includedCharacterListHeader(selectedCharacter: ReadOnlyObjectProperty<IncludedCharacterViewModel?>) {
        hbox {
            addClass(Styles.characterListHeader)
            addCharacterButton()
            spacer()
            characterOptionsButton(selectedCharacter)
        }
    }

    @ViewBuilder
    private fun Parent.addCharacterButton() = primaryMenuButton(props.addCharacterText) {
        addClass(Styles.addCharacterButton)
        controlledBy(logic::addCharacterButton)
        items.bindToAvailableCharacterOptions()
    }

    @ViewBuilder
    private fun ObservableList<MenuItem>.bindToAvailableCharacterOptions()
    {
        props.availableCharacters.onChangeWithCurrent {
            when {
                it == null -> setAll(loadingItem())
                it.isEmpty() -> setAll(createCharacterOption(), SeparatorMenuItem(), allCharactersUsedMessage())
                else -> setAll(createCharacterOption(), SeparatorMenuItem(), *(it.map(::availableCharacterOption)).toTypedArray())
            }
        }
    }

    private fun loadingItem(): MenuItem = MenuItem("Loading ... ").apply { isDisable = true }
    private fun allCharactersUsedMessage(): MenuItem = MenuItem("All characters in the story\nhave been included in the scene").apply {
        isDisable = true
    }
    private fun createCharacterOption(): MenuItem = MenuItem("Create New Character").apply {
        controlledBy(logic::createCharacterOption)
    }
    private fun availableCharacterOption(availableCharacter: AvailableCharacterToAddToSceneViewModel): MenuItem {
        return MenuItem(availableCharacter.name, characterIcon(availableCharacter.imageSource.toProperty())).apply {
            id = availableCharacter.id.toString()
            userData = availableCharacter.id
            controlledBy(logic::availableCharacterOption)
        }
    }

    @ViewBuilder
    private fun Parent.characterOptionsButton(selectedCharacter: ReadOnlyObjectProperty<IncludedCharacterViewModel?>) {
        menubutton {
            textProperty().bind(props.characterOptionsText)
            items.setAll(characterOptions(selectedCharacter))
            controlledBy(logic::characterOptionsButton)
        }
    }

    @ViewBuilder
    private fun Parent.includedCharacterList(selectedCharacter: ObjectProperty<IncludedCharacterViewModel?>): Node {
        val characterContextMenu = characterContextMenu(selectedCharacter)
        return listview<IncludedCharacterViewModel>(props.includedCharacters) {
            addClass(Styles.characterList)
            selectedCharacter.bind(selectionModel.selectedItemProperty())
            cellFormat {
                graphic = listedCharacterItem()
                    .apply { applyContextMenu(characterContextMenu) }
                applyFirstChildPseudoClasses()
            }
        }
    }

    @ViewBuilder
    private fun ListCell<IncludedCharacterViewModel>.listedCharacterItem(): Node? {
        /*
        this function is called each time the item updates in the cell.  By using the cache, we prevent the unnecessary
        rebuilding of the includedCharacterItemView and allow the previous one to remain bound to the itemProperty() of
        the listcell. It will automatically update the various text fields when the itemProperty() updates.
         */

        val cacheKey = "sceneCharactersView.characterList.cell.graphic"
        val previousNode = properties[cacheKey] as? Node
        val includedCharacterItem = item
        if (includedCharacterItem == null) {
            properties.remove(cacheKey)
            return null
        }
        if (previousNode != null) return previousNode
        val newNode = includedCharacterItem(itemProperty()) {
            actionText.set("Edit")
            actionButton {
                addClass(ComponentsStyles.secondary)
            }
            controlledBy(logic.listedCharacterItem(listView.parent))
        }
        properties[cacheKey] = newNode
        return newNode
    }

    private fun characterContextMenu(selectedCharacter: ObjectProperty<IncludedCharacterViewModel?>): ContextMenu {
        return ContextMenu().apply {
            items.setAll(characterOptions(selectedCharacter))
        }
    }

    private fun characterOptions(selectedCharacter: ReadOnlyObjectProperty<IncludedCharacterViewModel?>): List<MenuItem> {
        return listOf(
            MenuItem().apply {
                textProperty().bind(selectedCharacter.select { props.toggleIncitingCharacterText(it) })
                controlledBy(logic::incitingCharacterToggle)
            },
            MenuItem().apply {
                textProperty().bind(selectedCharacter.select { props.toggleOpponentCharacterText(it) })
                controlledBy(logic::opponentCharacterToggle)
            },
            MenuItem("Edit").controlledBy(logic::editCharacterOption),
            MenuItem("Remove from Scene").controlledBy(logic::removeCharacterOption)
        )
    }

    class Styles : Stylesheet()
    {
        companion object {
            val sceneCharacters by cssclass()
            val characterListHeader by cssclass()
            val characterList by cssclass()
            val addCharacterButton by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            characterListHeader {
                backgroundColor = multi(Color.WHITE)
                padding = box(16.px, 16.px, 8.px, 16.px)
            }
            characterList {
                removeListViewBorder()
                listCell {
                    backgroundColor = multi(Color.WHITE)
                    padding = box(0.px, 8.px)
                    and(selected) {
                        backgroundColor = multi(Color.web("#f2f2f2"))
                    }
                    and(notFirstChild) {
                        IncludedCharacterItemView.Styles.includedCharacterItem {
                            borderWidth = multi(box(1.px, 0.px, 0.px, 0.px))
                            borderColor = multi(box(Color.DARKGRAY, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT))
                        }
                    }
                }
            }
        }
    }

}

