package com.soyle.stories.desktop.config.features.storyevent

import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.confirm
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.showRamifications
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenRamificationsToolOrError
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenReportOrError
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventDetailsHaveBeenOpenedFor
import com.soyle.stories.desktop.config.drivers.storyevent.givenStoryEventListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.storyevent.removeCharacter
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptView
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptViewModel
import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportView
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptView
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.Labeled
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import org.junit.jupiter.api.Assertions
import tornadofx.uiComponent

class `Remove Character from Story Event Steps` : StoryEventFeatureSteps {

    init {
        Given(
            "I am removing the {character} from the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            getRemoveCharacterFromStoryEventPrompt(storyEvent.id, character.id) ?: run {
                workbench.givenStoryEventListToolHasBeenOpened()
                    .givenStoryEventDetailsHaveBeenOpenedFor(storyEvent.id)
                    .removeCharacter(character.id)
            }
            getRemoveCharacterFromStoryEventPromptOrError(storyEvent.id, character.id)
        }


        When("I stop involving the {character} in the {story event}") { character: Character, storyEvent: StoryEvent ->
            workbench.givenStoryEventListToolHasBeenOpened()
                .givenStoryEventDetailsHaveBeenOpenedFor(storyEvent.id)
                .removeCharacter(character.id)
            getRemoveCharacterFromStoryEventPrompt(storyEvent.id, character.id)
                ?.confirm()
        }
        When(
            "I show the ramifications of removing the {character} from the {story event}"
        ) { character: Character, storyEvent: StoryEvent ->
            workbench.givenStoryEventListToolHasBeenOpened()
                .givenStoryEventDetailsHaveBeenOpenedFor(storyEvent.id)
                .removeCharacter(character.id)
            getRemoveCharacterFromStoryEventPromptOrError(storyEvent.id, character.id)
                .showRamifications()
        }


        Then(
            "the following should be listed as ramifications of removing the {character} from the {story event}"
        ) { character: Character, storyEvent: StoryEvent, data: DataTable ->
            val ramifications = workbench.getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveCharacterFromStoryEventRamificationsReportView::class)
                .content!!.uiComponent<RemoveCharacterFromStoryEventRamificationsReportView>()!!

            val messages = ramifications.root.childrenUnmodifiable.map {
                it as TextFlow
                it.children.map {
                    when (it) {
                        is Text -> it.text
                        is Labeled -> it.text
                        else -> ""
                    }
                }.joinToString("")
            }
            data.asList().forEach {
                Assertions.assertTrue(messages.contains(it)) {
                    """
                        Did not find expected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
        Then(
            "the following should not be listed as ramifications of removing the {character} from the {story event}"
        ) { character: Character, storyEvent: StoryEvent, data: DataTable ->
            val ramifications = workbench.getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveCharacterFromStoryEventRamificationsReportView::class)
                .content!!.uiComponent<RemoveCharacterFromStoryEventRamificationsReportView>()!!

            val messages = ramifications.root.childrenUnmodifiable.map {
                it as TextFlow
                it.children.map {
                    when (it) {
                        is Text -> it.text
                        is Labeled -> it.text
                        else -> ""
                    }
                }.joinToString("")
            }
            data.asList().forEach {
                Assertions.assertFalse(messages.contains(it)) {
                    """
                        Found unexpected message in ramifications.
                            Expected: $it
                                  In: $messages
                    """.trimIndent()
                }
            }
        }
    }

    private fun getRemoveCharacterFromStoryEventPromptOrError(storyEventId: StoryEvent.Id, characterId: Character.Id): RemoveCharacterFromStoryEventPromptView {
        return getRemoveCharacterFromStoryEventPrompt(storyEventId, characterId) ?: error("Remove Character from Story Event Prompt has not been opened")
    }
    private fun getRemoveCharacterFromStoryEventPrompt(storyEventId: StoryEvent.Id, characterId: Character.Id): RemoveCharacterFromStoryEventPromptView? {
        return robot.listWindows()
            .asSequence()
            .filter { it.isShowing }
            .mapNotNull { it.scene.root.uiComponent<RemoveCharacterFromStoryEventPromptView>() }
            .filter { it.viewModel.storyEventId == storyEventId && it.viewModel.characterId == characterId }
            .firstOrNull()
    }

}