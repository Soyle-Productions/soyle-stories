package com.soyle.stories.scene.characters.include.selectCharacter

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.SelectCharacterPrompt
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.scene.Node
import javafx.stage.Window
import javafx.stage.WindowEvent
import javafx.util.StringConverter
import tornadofx.bind

@ViewBuilder
fun Node.selectCharacterPrompt(
    viewModel: SelectCharacterPromptViewModel = SelectCharacterPromptViewModel(),
    configure: Window.() -> Unit = Window::applyNothing
): SelectCharacterPrompt {
    val owner = this

    properties["selectCharacterPrompt"] = AutoCompletePopup<CharacterSuggestion>().apply {
        scopedListener(viewModel.items) {
            suggestions.setAll(listOf(CharacterSuggestion(null)) + it.orEmpty().map { CharacterSuggestion(it) })
        }
        converter = availableCharacterItemStringConverter(suggestions)
        setOnSuggestion { viewModel.select(it.suggestion.item) }
        scopedListener(viewModel.isOpen()) { if (it == true) show(owner) else hide() }
        addEventHandler(WindowEvent.WINDOW_HIDDEN) { viewModel.cancel() }
        configure()
    }
    return viewModel
}

class CharacterSuggestion(val item: CharacterItem?)

private fun availableCharacterItemStringConverter(items: List<CharacterSuggestion>) =
    object : StringConverter<CharacterSuggestion>() {
        override fun fromString(string: String?): CharacterSuggestion? {
            if (string == null) return null
            return items.find { it.item?.characterName == string }
        }

        override fun toString(`object`: CharacterSuggestion?): String = `object`?.run {
            item?.characterName ?: "Create New Character"
        } ?: ""
    }