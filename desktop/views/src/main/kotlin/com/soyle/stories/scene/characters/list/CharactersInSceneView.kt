package com.soyle.stories.scene.characters.list

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.dataDisplay.list.ListStyles
import com.soyle.stories.common.components.dataDisplay.list.ListStyles.Companion.applyFirstChildPseudoClasses
import com.soyle.stories.common.components.dataDisplay.list.ListStyles.Companion.removeListViewBorder
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.markdown.contentAsMarkdown
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.scene.characters.tool.SceneCharactersInviteImageView.Companion.sceneCharactersInviteImage
import com.soyle.stories.scene.characters.include.selectCharacter.selectCharacterPrompt
import com.soyle.stories.scene.characters.list.item.*
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.WindowEvent
import tornadofx.*



fun CharactersInSceneListLocale.CharactersInScene(
    viewModel: CharactersInSceneViewModel
): Node {
    return CharactersInSceneView(
        this,
        viewModel
    )
}

@ViewBuilder
fun Parent.charactersInScene(
    locale: CharactersInSceneListLocale,
    viewModel: CharactersInSceneViewModel,
): Node {
    return locale.CharactersInScene(viewModel).also { add(it) }
}

private class CharactersInSceneView(
    private val locale: CharactersInSceneListLocale,

    override val viewModel: CharactersInSceneViewModel
) : StackPane(), ViewOf<CharactersInSceneViewModel> {

    private val noCharacters: BooleanBinding = viewModel.characters.emptyProperty()

    private val characterIsSelected = viewModel.selectedCharacter.booleanBinding { it != null }

    init {
        addClass(CharactersInSceneStyles.characterList)
        dynamicContent(noCharacters) {
            if (noCharacters.value) noIncludedCharacters()
            else charactersIncluded()
        }
    }

    @ViewBuilder
    private fun StackPane.noIncludedCharacters() {
        invitation {
            noIncludedCharactersMessage()
            addCharacterButton().apply { addClass(ButtonStyles.inviteButton) }
        }
    }

    @ViewBuilder
    private fun StackPane.charactersIncluded() {
        vbox {
            includedCharacterListHeader()
            includedCharacterList().apply { vgrow = Priority.ALWAYS }
        }
    }

    @ViewBuilder
    private fun Parent.invitation(message: @ViewBuilder Parent.() -> Unit): Node {
        return emptyToolInvitation {
            sceneCharactersInviteImage()
            toolTitle { textProperty().bind(locale.toolTitle) }
            message()
        }
    }

    @ViewBuilder
    private fun Parent.noIncludedCharactersMessage() {
        textflow {
            textAlignment = TextAlignment.CENTER
            contentAsMarkdown(locale.noCharactersInScene_inviteMessage)
        }
    }

    @ViewBuilder
    private fun Parent.addCharacterButton(): Button = primaryButton(locale.addCharacter) {
        val isShowing = booleanProperty(false)

        addClass(CharactersInSceneStyles.addCharacterButton)
        action { isShowing.set(true) }
        scopedListener(isShowing) {
            when (it) {
                false, null -> {}
                true -> {
                    viewModel.onAddCharacter(
                        selectCharacterPrompt {
                            addEventHandler(WindowEvent.WINDOW_HIDDEN) { isShowing.set(false) }
                        }
                    )
                }
            }
        }
    }

    @ViewBuilder
    private fun VBox.includedCharacterListHeader() {
        hbox {
            addClass(CharactersInSceneStyles.characterListHeader)
            elevationProperty().bind(this@CharactersInSceneView.elevationProperty())
            elevationVariant = outlined
            addCharacterButton()
            spacer()
            characterOptionsButton()
        }
    }

    @ViewBuilder
    private fun Parent.includedCharacterList(): Node {
        val characterContextMenu = characterContextMenu()
        return listview<CharacterInSceneItemViewModel> {
            addClass(ListStyles.noCellShading)
            items.bind(viewModel.characters) { it }
            scopedListener(viewModel.selectedCharacter) {
                if (it == null) selectionModel.clearSelection()
                else selectionModel.select(it)
            }
            selectionModel.selectedItemProperty().onChange { viewModel.onSelectCharacter(it) }
            cellFormat {
                val graphicCache: Pair<Character.Id, Node>? = properties["com.soyle.stories.cellCache"] as? Pair<Character.Id, Node>
                graphic = if (graphicCache?.first == it.character) graphicCache.second
                else {
                    CharacterInSceneItem(it) {
                        toggleClass(Stylesheet.selected, this@cellFormat.selectedProperty())

                        button(locale.item.menu.edit) {
                            addClass(ComponentsStyles.secondary)
                            action { viewModel.onEditCharacter(it) }
                        }
                    }
                }
                properties["com.soyle.stories.cellCache"] = it.character to graphic
                contextMenu = characterContextMenu
                applyFirstChildPseudoClasses()
            }
        }
    }

    @ViewBuilder
    private fun Parent.characterOptionsButton() {
        menubutton {
            addClass(ComponentsStyles.secondary, ComponentsStyles.outlined, ButtonStyles.noArrow)
            textProperty().bind(locale.options)
            items.setAll(characterOptions())
            enableWhen(characterIsSelected)
        }
    }

    private fun characterContextMenu(): ContextMenu {
        return ContextMenu().apply {
            items.setAll(characterOptions())
        }
    }

    private fun characterOptions(): List<MenuItem> {
        val menuLocale = locale.item.menu
        return listOf(
            MenuItem().apply {
                textProperty().bind(menuLocale.toggleIncitingCharacter)
                action { viewModel.onToggleRole(RoleInScene.IncitingCharacter) }
            },
            MenuItem().apply {
                textProperty().bind(menuLocale.toggleOpponentCharacter)
                action { viewModel.onToggleRole(RoleInScene.OpponentCharacter) }
            },
            MenuItem().apply {
                textProperty().bind(menuLocale.edit)
                action { viewModel.selectedCharacter.value?.let(viewModel.onEditCharacter) }
            },
            MenuItem().apply {
                textProperty().bind(menuLocale.removeCharacterFromScene)
                action { viewModel.selectedCharacter.value?.let(viewModel.onRemoveCharacter) }
            }
        )
    }

    private fun allCharactersUsedMessage(): MenuItem =
        MenuItem("All characters in the story\nhave been included in the scene").apply {
            isDisable = true
        }

    private fun createCharacterOption(): MenuItem = MenuItem("Create New Character").apply {
       // controlledBy(logic::createCharacterOption)
    }


}
class CharactersInSceneStyles : Stylesheet() {
    companion object {
        val characterListHeader by cssclass()
        val characterList by cssclass()
        val addCharacterButton by cssclass()

        init {
            styleImporter<CharactersInSceneStyles>()
        }
    }

    init {
        characterListHeader {
            padding = box(16.px, 16.px, 12.px, 16.px)
        }
        characterList {
            listView {
                removeListViewBorder()
                backgroundColor = multi(Color.TRANSPARENT)
                listCell {
                    padding = box(0.px, 8.px)
                    and(ComponentsStyles.notFirstChild) {
                        CharacterInSceneItemStyles.characterInSceneItem {
                            borderWidth = multi(box(1.px, 0.px, 0.px, 0.px))
                            borderColor =
                                multi(
                                    box(
                                        SurfaceStyles.lightBackground(1.0),
                                        Color.TRANSPARENT,
                                        Color.TRANSPARENT,
                                        Color.TRANSPARENT
                                    )
                                )
                            and(selected) {
                                borderColor = multi(box(Color.TRANSPARENT))
                            }
                            and(hover) {
                                borderColor = multi(box(Color.TRANSPARENT))
                            }
                        }
                    }
                }
            }
        }
    }
}