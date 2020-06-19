package com.soyle.stories.character

import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En

class CharacterSteps(en: En, double: SoyleStoriesTestDouble) {

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

		}
	}

}