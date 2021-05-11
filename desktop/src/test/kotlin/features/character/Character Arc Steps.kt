package com.soyle.stories.desktop.config.features.character

import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialog
import com.soyle.stories.desktop.config.drivers.character.*
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNull

class `Character Arc Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I am creating a character arc for the {character}") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCreateCharacterArcDialogHasBeenOpened(character.id)
        }
        Given(
            "I have created a character arc named {string} for the {character}"
        ) { arcName: String, character: Character ->
            CharacterArcDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterArcNamed(character.id, arcName)
        }
    }

    private fun whens() {
        When(
            "I delete the {string} character arc for the {character}"
        ) { arcName: String, character: Character ->
            val arc = CharacterArcDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterArcNamed(character.id, arcName)!!

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .deleteCharacterArc(character.id, arc.themeId)
        }

        When(
            "I create a character arc named {string} for the {character}"
        ) { arcName: String, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCreateCharacterArcDialogHasBeenOpened(character.id)
                .createCharacterArcNamed(arcName)
        }
    }

    private fun thens() {
        Then(
            "a character arc named {string} should have been created for the {character}"
        ) { arcName: String, character: Character ->
            CharacterArcDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterArcNamed(character.id, arcName)
                .let(Assertions::assertNotNull)
        }
        Then(
            "the {character} should not have a {string} character arc"
        ) { character: Character, arcName: String ->
            CharacterArcDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterArcNamed(character.id, arcName)
                .let(Assertions::assertNull)
        }
        Then("I should not be creating a character arc") {
            assertNull(robot.getOpenDialog<PlanCharacterArcDialog>())
        }
    }

}