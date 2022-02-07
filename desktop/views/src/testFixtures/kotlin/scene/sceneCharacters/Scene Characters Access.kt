package com.soyle.stories.desktop.view.scene.sceneCharacters

import com.soyle.stories.common.ViewOf
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.desktop.view.common.asViewOf
import com.soyle.stories.desktop.view.common.maybeViewOf
import com.soyle.stories.desktop.view.scene.sceneCharacters.list.CharacterInSceneItemViewAccess
import com.soyle.stories.desktop.view.scene.sceneCharacters.list.CharactersInSceneListAccess
import com.soyle.stories.desktop.view.scene.sceneCharacters.list.access
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel
import com.soyle.stories.scene.characters.include.selectCharacter.CharacterSuggestion
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionStyles
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import com.soyle.stories.scene.characters.list.CharactersInSceneStyles
import com.soyle.stories.scene.characters.list.CharactersInSceneViewModel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldListCell
import tornadofx.CssRule

class `Scene Characters Access`(private val view: SceneCharactersToolComponent) : NodeAccess<Parent>(view.root) {

    companion object {
        fun SceneCharactersToolComponent.access() = `Scene Characters Access`(this)
        fun <R> SceneCharactersToolComponent.drive(block: `Scene Characters Access`.() -> R): R {
            val driver = access()
            var r: Any? = Any()
            driver.interact {
                r = driver.block()
            }
            return r as R
        }
    }

    private val viewModel
        get() = view.viewModel

    fun isFocusedOn(scene: Scene): Boolean {
        return (viewModel.sceneSelection.value as? SceneCharactersToolViewModel.SceneSelection.Selected)?.sceneId == scene.id
    }

    fun isInspecting(characterId: Character.Id): Boolean {
        return viewModel.focusedCharacter.value?.character == characterId
    }

    private val characterList: CharactersInSceneListAccess?
        get() = node.findChild<Node>(CharactersInSceneStyles.characterList)
            ?.maybeViewOf<CharactersInSceneViewModel>(logFailure = true)
            ?.access()

    fun getCharacterItem(characterId: Character.Id): CharacterInSceneItemViewAccess? =
        characterList?.getCharacterItem(characterId)

    fun getCharacterItemOrError(characterId: Character.Id): CharacterInSceneItemViewAccess =
        getCharacterItem(characterId) ?: throw NoSuchElementException("did not find $characterId in $characterList")

    fun getCharacterItemByName(characterName: String): CharacterInSceneItemViewAccess? =
        characterList?.getCharacterItemByName(characterName)

    val includeCharacterSelection by temporaryChild<Button>(CharactersInSceneStyles.addCharacterButton)

    val includeCharacterPopup: AutoCompletePopup<*>?
        get() {
            val addCharacterButton = includeCharacterSelection
            return listWindows()
                .filterIsInstance<AutoCompletePopup<*>>()
                .find { it.ownerWindow == addCharacterButton?.scene?.window }
        }

    val availableStoryEventItems: List<ListCell<StoryEventItem>>
        get() {
            val autocomplete = includeCharacterPopup ?: return emptyList()
            return autocomplete.scene.root.findChildren<TextFieldListCell<StoryEventItem>>(CssRule.c("text-field-list-cell"))
        }

    fun getAvailableStoryEventItem(storyEventId: StoryEvent.Id): ListCell<StoryEventItem>? {
        val addCharacterButton = includeCharacterSelection
        val autocomplete = listWindows()
            .filterIsInstance<AutoCompletePopup<*>>()
            .find { it.isShowing && it.ownerWindow == addCharacterButton?.scene?.window }
            ?: return null
        return autocomplete.scene.root.findChild<TextFieldListCell<StoryEventItem>>(CssRule.c("text-field-list-cell")) {
            (it.item as? StoryEventItem)?.storyEventId == storyEventId
        }
    }

    val createNewCharacterItem: ListCell<CharacterSuggestion>
        get() {
            val autocomplete = includeCharacterPopup!!
            return autocomplete.scene.root.findChild<TextFieldListCell<CharacterSuggestion>>(
                CssRule.c("text-field-list-cell")
            ) {
                it.item != null && it.item?.item == null
            }!!
        }

    val availableCharacterItems: List<ListCell<CharacterSuggestion>>
        get() {
            val autocomplete = includeCharacterPopup ?: return emptyList()
            return autocomplete.scene.root.findChildren<TextFieldListCell<CharacterSuggestion>>(
                CssRule.c("text-field-list-cell")
            ).filter { it.item?.item != null }
        }

    fun getAvailableCharacterItem(character: Character): ListCell<CharacterSuggestion>? {
        val autocomplete = listWindows()
            .filterIsInstance<AutoCompletePopup<*>>()
            .find { it.isShowing && it.suggestions.any { it is CharacterSuggestion } }
            ?: return null
        return autocomplete.scene.root
            .findChild<TextFieldListCell<CharacterSuggestion>>(CssRule.c("text-field-list-cell")) {
                it.item?.item?.characterId == character.id.uuid
            }
    }


    val characterInspection: ViewOf<CharacterInSceneInspectionViewModel>?
        get() = view.root.findChild<Node>(CharacterInSceneInspectionStyles.characterEditor) {
            it.maybeViewOf<CharacterInSceneInspectionViewModel>() != null
        }?.asViewOf()

    val ViewOf<CharacterInSceneInspectionViewModel>.removeButton
        get() = (this as Node).findChild<Button>(CssRule.id("remove-from-scene"))!!

    val ViewOf<CharacterInSceneInspectionViewModel>.positionOnArcSelection
        get() = from(this as Node).lookup("#coverArcSectionButton").query<MenuButton>()

    val ViewOf<CharacterInSceneInspectionViewModel>.desireInput
        get() = from(this as Node).lookup(".desire .text-field").queryTextInputControl()

    val ViewOf<CharacterInSceneInspectionViewModel>.motivationInput
        get() = from(this as Node).lookup(".motivation .text-field").queryTextInputControl()

    private val ViewOf<CharacterInSceneInspectionViewModel>.roleInSceneSelection
        get() = from(this as Node).lookup(".${CharacterInSceneInspectionStyles.roleInSceneSelection.name}").queryParent()

    val ViewOf<CharacterInSceneInspectionViewModel>.incitingCharacterToggle
        get() = from(roleInSceneSelection).lookup("Is Inciting Character").query<ButtonBase>()

    val ViewOf<CharacterInSceneInspectionViewModel>.opponentCharacterToggle
        get() = from(roleInSceneSelection).lookup("Is Opponent to Inciting Character").query<ButtonBase>()

    fun MenuButton.getSectionItem(arcName: String, sectionName: String): MenuItem? {
        val arcMenu = items.find { it.text == arcName } as? Menu
        return arcMenu?.items?.find { it.text == sectionName }
    }

    fun MenuButton.getSectionItemOrError(arcName: String, sectionName: String): MenuItem {
        return getSectionItem(arcName, sectionName)
            ?: error("Could not find section ${sectionName} for ${arcName} in ${items.map { "${it.text}: [${(it as? Menu)?.items?.map { it.text }}]" }}")
    }

    fun MenuButton.getCreateArcSectionOption(arcName: String): MenuItem? {
        val arcMenu = items.find { it.text == arcName } as? Menu
        return arcMenu?.items?.find { it.text == "Create New Character Arc Section" }
    }

    fun MenuButton.getCreateArcSectionOptionOrError(arcName: String): MenuItem =
        getCreateArcSectionOption(arcName)
            ?: error("Could not find creation option for ${arcName} in ${items.map { "${it.text}: [${(it as? Menu)?.items?.map { it.text }}]" }}")

    fun ViewOf<CharacterInSceneInspectionViewModel>.haveAvailableArcsToCoverBeenRequested(): Boolean {
        return positionOnArcSelection.isShowing
    }
}