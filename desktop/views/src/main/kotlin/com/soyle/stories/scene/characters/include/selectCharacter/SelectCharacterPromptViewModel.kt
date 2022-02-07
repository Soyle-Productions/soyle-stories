package com.soyle.stories.scene.characters.include.selectCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.SelectCharacterPrompt
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.observableListOf

class SelectCharacterPromptViewModel : SelectCharacterPrompt {

    private val _isOpen = booleanProperty(false)
    fun isOpen(): ReadOnlyBooleanProperty = _isOpen

    private val _items = observableListOf<CharacterItem>()
    val items: ObservableList<CharacterItem> = FXCollections.unmodifiableObservableList(_items)

    private var _onSelection: (SelectCharacterPrompt.CharacterSelection) -> Unit = {}

    fun select(item: CharacterItem?) {
        val selection = when (item) {
            null -> SelectCharacterPrompt.CharacterSelection.CreateNew
            else -> SelectCharacterPrompt.CharacterSelection.Selected(Character.Id(item.characterId))
        }
        _onSelection(selection)
        _isOpen.set(false)
    }

    private var _onCancel: () -> Unit = {}
    fun cancel() {
        _onCancel()
        _isOpen.set(false)
    }

    override suspend fun selectCharacter(availableCharacters: AvailableCharactersToAddToScene): SelectCharacterPrompt.CharacterSelection? {
        _items.setAll(availableCharacters)
        val selection = CompletableDeferred<SelectCharacterPrompt.CharacterSelection?>()
        _onSelection = {
            selection.complete(it)
        }
        _onCancel = {
            selection.complete(null)
        }
        _isOpen.set(true)
        return selection.await()
    }

    override suspend fun done() {
        _isOpen.set(false)
    }

}