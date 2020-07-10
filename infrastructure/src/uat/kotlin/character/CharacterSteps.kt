package com.soyle.stories.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterNotifier
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.fail
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

class CharacterSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object : ApplicationTest()

	init {
		CharacterComparisonSteps(en, double)
		with(en) {

			Given("a Character has been created") {
				CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, 1)
			}
			Given("no Characters have been created") {
				// no-op
			}
			Given("a Character Arc has been created") {
				CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
			}
			Given("{int} Characters have been created") { int1: Int ->
				CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, int1)
			}
			Given("The Character List Tool has been opened") {
				// LocationListDriver.givenHasBeenOpened(double)
				CharacterDriver.givenCharacterListToolHasBeenOpened(double)
			}
			Given("A Character has been created") {
				CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, 1)
			}
			Given("the Character right-click menu has been opened") {
				CharacterDriver.givenCharacterListToolCharacterContextMenuHasBeenOpened(double)
			}
			Given("a Character has been selected") {
				CharacterDriver.givenCharacterIsSelectedInCharacterListTool(double)
			}
			Given("the Character rename input box is visible") {
				CharacterDriver.givenCharacterListToolShowingInputBoxForSelectedItem(double)
			}
			Given("the user has entered a valid Character name") {
				CharacterDriver.givenValidCharacterNameHasBeenEnteredInCharacterListToolCharacterRenameInputBox(double)
			}
			Given("the user has entered an invalid Character name") {
				CharacterDriver.givenInvalidCharacterNameHasBeenEnteredInCharacterListToolCharacterRenameInputBox(double)
			}
			Given("the Create Character Dialog has been opened") {
				CreateCharacterDialogDriver.givenHasBeenOpened(double)
			}
			Given("the Create Character Dialog Name input has an invalid Character Name") {
				CreateCharacterDialogDriver.givenNameInputHasInvalidCharacterName(double)
			}
			Given("the Create Character Dialog Name input has a valid Character Name") {
				CreateCharacterDialogDriver.givenNameInputHasValidCharacterName(double)
			}
			Given("a Character called {string} has been created") {
				characterName: String ->

				CharacterDriver.getCharacterByIdentifier(double, characterName) ?: run {
					val scope = ProjectSteps.givenProjectHasBeenOpened(double)
					val useCase = scope.get<BuildNewCharacter>()
					runBlocking {
						useCase.invoke(characterName, object : BuildNewCharacter.OutputPort {
							override fun receiveBuildNewCharacterFailure(failure: CharacterException) = throw failure
							override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
								CharacterDriver.registerIdentifiers(double, listOf(characterName to Character.Id(response.characterId)))
							}
						})
					}

				}
			}

			When("the Character {string} is renamed to {string}") {
				characterIdentifier: String, newName: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				CharacterDriver.whenCharacterIsRenamed(double, characterId, newName)
			}
			When("the Character {string} is deleted") {
				characterIdentifier: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				CharacterDriver.whenCharacterIsDeleted(double, characterId)
			}
			When("a Character called {string} is created") {
					characterName: String ->
				val scope = ProjectSteps.getProjectScope(double)!!
				val notifier = scope.get<BuildNewCharacterNotifier>()
				val controller = scope.get<BuildNewCharacterController>()
				interact {
					runBlocking {
						val output = object : BuildNewCharacter.OutputPort {
							override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
								CharacterDriver.registerIdentifiers(
									double,
									listOf(characterName to Character.Id(response.characterId))
								)
							}

							override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}
						}
						notifier.addListener(output)
						controller.buildNewCharacter(characterName) { throw it }
						notifier.removeListener(output)
					}
				}
			}
			When("the character {string} is renamed to {string}") { ogName: String, newName: String ->
				val character = CharacterDriver.getCharacterByIdentifier(double, ogName)!!
				interact {
					CharacterDriver.whenCharacterIsRenamed(double, character.id, newName)
				}
			}
			When("the character {string} is removed from the story") { characterName: String ->
				val character = CharacterDriver.getCharacterByIdentifier(double, characterName)!!
				interact {
					CharacterDriver.whenCharacterIsDeleted(double, character.id)
				}
			}

		}
	}

}