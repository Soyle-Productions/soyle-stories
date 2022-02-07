package com.soyle.stories.desktop.config.features.character

import com.soyle.stories.character.delete.ramifications.RemoveCharacterRamificationsReportView
import com.soyle.stories.desktop.config.drivers.character.getDeleteCharacterDialogOrError
import com.soyle.stories.desktop.config.drivers.character.givenCharacterListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.character.openDeleteCharacterDialog
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.confirm
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.showRamifications
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenRamificationsToolOrError
import com.soyle.stories.desktop.config.drivers.ramifications.getOpenReportOrError
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.di.DI.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportView
import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportView
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNull
import tornadofx.uiComponent

class `Remove Character from Story Steps` : En {

    init {
        Given("I have requested not to be prompted to confirm removing characters from the story") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .scope.get<SetDialogPreferencesController>()
                .setDialogPreferences("DeleteCharacter", false)
        }


        When("I want to remove the {character} from the story") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .openDeleteCharacterDialog(character.id)
        }
        When("I show the ramifications of removing the {character} from the story") { character: Character ->
            getDeleteCharacterDialogOrError()
                .showRamifications()
        }
        When("I confirm that I want to remove the {character} from the story") { character: Character ->
            getDeleteCharacterDialogOrError()
                .confirm()
        }


        Then("the {character} should not be in the project") { character: Character ->
            assertNull(character.projectId)
        }
        Then("I should be prompted to confirm removing the {character} from the story") { character: Character ->
            getDeleteCharacterDialogOrError()
        }
        Then("nothing should be listed as ramifications of removing the {character} from the story") { character: Character ->
            val ramifications = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveCharacterRamificationsReportView::class)
                .content!!.uiComponent<RemoveCharacterRamificationsReportView>()!!

            ramifications.root.childrenUnmodifiable.single { it is Label }
        }
        Then(
            "the following should be listed as ramifications of removing the {character} from the story"
        ) { character: Character, data: DataTable ->
            val ramifications = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenRamificationsToolOrError()
                .getOpenReportOrError(RemoveCharacterRamificationsReportView::class)
                .content!!.uiComponent<RemoveCharacterRamificationsReportView>()!!

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
    }

}