package com.soyle.stories.storyevent.details

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.builders.build
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.notNull
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.character.remove.ramifications.removeCharacterFromStoryEventRamifications
import com.soyle.stories.storyevent.character.remove.removeCharacterFromStoryEventPrompt
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TextField
import tornadofx.*

fun StoryEventDetails(
    viewModel: StoryEventDetailsViewModel
): Parent {
    return Form().apply {
        disableWhen(viewModel.isLoading)
    }.build {
        sectionTitle(viewModel.name)

        fieldset(text = "Details", labelPosition = Orientation.VERTICAL) {
            field("Location") {
                label(viewModel.locationName())
            }
            field("Characters") {
                characterSelection(
                    availableCharacters = viewModel.availableCharacters(),
                    onLoadAvailableCharacters = viewModel.loadAvailableCharacters,
                    onCancelSelection = viewModel.cancelSelection,
                    onSelected = viewModel.selectCharacter
                )
                vbox {
                    bindChildren(viewModel.characters()) { character ->
                        hbox {
                            idProperty().bind(character.id().stringBinding { it?.uuid.toString() })
                            label(character.name())
                            button("Remove") {
                                action {
                                    viewModel.removeCharacter(
                                        character.id,
                                        scene?.window
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@ViewBuilder
private fun Parent.characterSelection(
    availableCharacters: ObservableValue<AvailableCharactersToInvolveInStoryEvent?>,
    onLoadAvailableCharacters: () -> Unit,
    onCancelSelection: () -> Unit,
    onSelected: (AvailableStoryElementItem<Character.Id>) -> Unit
): Node {
    val autoCompletePopup = AutoCompletePopup<AvailableStoryElementItem<Character.Id>>().apply {
        setOnSuggestion { onSelected(it.suggestion) }
        setOnHidden { onCancelSelection() }
    }

    return textfield {
        id = "character-selection"
        val textChangeListener = ChangeListener { _, _, text: String? ->
            val availableCharacters = availableCharacters.value ?: return@ChangeListener
            when (val query = NonBlankString.create(text ?: "")) {
                null -> autoCompletePopup.suggestions.setAll(availableCharacters.allAvailableElements)
                else -> autoCompletePopup.suggestions.setAll(availableCharacters.getMatches(query))
            }
        }
        focusedProperty().onChange {
            when (it) {
                false -> {
                    textProperty().removeListener(textChangeListener)
                    autoCompletePopup.hide()
                }
                true -> {
                    availableCharacters.onChangeUntil(::notNull) { availableCharacters ->
                        if (availableCharacters != null) {
                            textProperty().addListener(textChangeListener)
                            textChangeListener.changed(textProperty(), text, text)
                        }
                    }
                    onLoadAvailableCharacters()
                    autoCompletePopup.show(this)
                }
            }
        }
    }
}