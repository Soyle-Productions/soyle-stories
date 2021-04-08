package com.soyle.stories.desktop.view.scene.sceneCharacters

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroup
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroupStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.sceneCharacters.*
import com.soyle.stories.scene.sceneCharacters.characterEditor.SelectedSceneCharacterEditor
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import org.testfx.api.FxRobot
import tornadofx.UI_COMPONENT_PROPERTY
import tornadofx.uiComponent

class `Scene Characters Driver`(private val view: SceneCharactersView) : FxRobot() {

    fun isFocusedOn(scene: Scene): Boolean {
        return view.scope.get<SceneCharactersState>().selectedSceneId.value == scene.id
    }

    fun isEditing(characterId: Character.Id): Boolean {
        return view.scope.get<SceneCharactersState>().characterBeingEdited.value?.id == characterId
    }

    fun getCharacterItemOrError(characterId: Character.Id): IncludedCharacterItemView =
        getCharacterItem(characterId) ?: error("No Character with id ${characterId} in Scene Characters tool")

    val includeCharacterSelection
        get() = from(view.root).lookup(".${SceneCharactersView.Styles.addCharacterButton.name}").query<MenuButton>()

    fun getAvailableCharacterItem(character: Character): MenuItem?
    {
        return includeCharacterSelection.items.find { it.id == character.id.toString() }
    }

    private val characterList: ListView<IncludedCharacterViewModel>?
        get() = from(view.root)
            .lookup(".${SceneCharactersView.Styles.characterList.name}")
            .queryAll<ListView<IncludedCharacterViewModel>>()
            .firstOrNull()

    fun getCharacterItem(characterId: Character.Id): IncludedCharacterItemView? {
        val characterList = characterList ?: return null
        return from(characterList).lookup(".${IncludedCharacterItemView.Styles.includedCharacterItem.name}")
            .queryAll<Parent>()
            .toList().asSequence()
            .mapNotNull { it.properties[UI_COMPONENT_PROPERTY] as? IncludedCharacterItemView }
            .filter { it.props.item.id == characterId }
            .firstOrNull()
    }

    fun getCharacterItemByName(characterName: String): IncludedCharacterItemView? {
        val characterList = characterList ?: return null
        return from(characterList).lookup(".${IncludedCharacterItemView.Styles.includedCharacterItem.name}").queryAll<Parent>()
            .asSequence()
            .mapNotNull { it.properties[UI_COMPONENT_PROPERTY] as? IncludedCharacterItemView }
            .filter { it.props.item.name == characterName }
            .firstOrNull()
    }

    val IncludedCharacterItemView.editButton: Button
        get() = from(root).lookup("Edit").queryButton()!!

    val IncludedCharacterItemView.role: Labeled
        get() = from(root).lookup(".${TextStyles.caption.name}").queryLabeled()!!

    fun getCharacterEditorOrError(): SelectedSceneCharacterEditor =
        getCharacterEditor() ?: error("No Character in Scene Editor is currently open in Scene Characters tool")

    fun getCharacterEditor(): SelectedSceneCharacterEditor? {
        return from(view.root)
            .lookup(".${SelectedSceneCharacterEditor.Styles.characterEditor.name}")
            .queryAll<Node>()
            .mapNotNull { it.uiComponent<SelectedSceneCharacterEditor>() }
            .firstOrNull()
    }

    val SelectedSceneCharacterEditor.removeButton
        get() = from(root).lookup("#remove-from-scene").queryButton()

    val SelectedSceneCharacterEditor.positionOnArcSelection
        get() = from(root).lookup("#coverArcSectionButton").query<MenuButton>()

    val SelectedSceneCharacterEditor.motivationInput
        get() = from(root).lookup(".motivation .text-field").queryTextInputControl()

    private val SelectedSceneCharacterEditor.roleInSceneSelection
        get() = from(root).lookup(".${SelectedSceneCharacterEditor.Styles.roleInSceneSelection.name}").queryParent()

    val SelectedSceneCharacterEditor.incitingCharacterToggle
        get() = from(roleInSceneSelection).lookup("Is Inciting Character").query<ButtonBase>()

    val SelectedSceneCharacterEditor.opponentCharacterToggle
        get() = from(roleInSceneSelection).lookup("Is Opponent to Inciting Character").query<ButtonBase>()

    fun MenuButton.getSectionItem(arcName: String, sectionName: String): MenuItem?
    {
        val arcMenu = items.find { it.text == arcName } as? Menu
        return arcMenu?.items?.find { it.text == sectionName }
    }
    fun MenuButton.getSectionItemOrError(arcName: String, sectionName: String): MenuItem
    {
        return getSectionItem(arcName, sectionName) ?: error("Could not find section ${sectionName} for ${arcName} in ${items.map { "${it.text}: [${(it as? Menu)?.items?.map { it.text }}]" }}")
    }

    fun MenuButton.getCreateArcSectionOption(arcName: String): MenuItem? {
        val arcMenu = items.find { it.text == arcName } as? Menu
        return arcMenu?.items?.find { it.text == "Create New Character Arc Section" }
    }
    fun MenuButton.getCreateArcSectionOptionOrError(arcName: String): MenuItem =
        getCreateArcSectionOption(arcName) ?: error("Could not find creation option for ${arcName} in ${items.map { "${it.text}: [${(it as? Menu)?.items?.map { it.text }}]" }}")

    fun SelectedSceneCharacterEditor.haveAvailableArcsToCoverBeenRequested(): Boolean
    {
        return positionOnArcSelection.isShowing
    }
}

fun SceneCharactersView.driver() = `Scene Characters Driver`(this)
fun <R> SceneCharactersView.drive(block: `Scene Characters Driver`.() -> R) : R {
    val driver = driver()
    var r: Any? = Any()
    driver.interact {
        r = driver.block()
    }
    return r as R
}