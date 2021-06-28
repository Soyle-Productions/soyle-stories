package com.soyle.stories.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.ThemeSteps
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.repositories.CharacterArcRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.testfx.framework.junit5.ApplicationTest

class CharacterSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object : ApplicationTest()
	{
		fun givenANumberOfCharacterArcsHaveBeenCreatedForCharacter(
			projectScope: ProjectScope, character: Character, count: Int
		): List<CharacterArc>
		{
			val repository = projectScope.get<CharacterArcRepository>()
			val existingArcs = runBlocking { repository.listCharacterArcsForCharacter(character.id) }
			if (existingArcs.size < count) {
				repeat(count - existingArcs.size) {
					projectScope.get<PlanNewCharacterArcController>()
						.planCharacterArc(character.id.uuid.toString(), "Some Arc ${Math.random()}") { throw it }
				}
			}
			return runBlocking { repository.listCharacterArcsForCharacter(character.id) }.take(count)
		}
	}

	init {
		CharacterValueComparisonSteps(en, double)
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
			Given("{int} Character Arcs have been created for the Character {string}") { arcCount: Int, characterName: String ->
				val scope = ProjectSteps.givenProjectHasBeenOpened(double)
				val character = CharacterDriver.givenACharacterHasBeenCreatedWithTheName(double, characterName)
				givenANumberOfCharacterArcsHaveBeenCreatedForCharacter(scope, character, arcCount)
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
					val controller = scope.get<BuildNewCharacterController>()
					interact {
						controller.createCharacter(characterName) { throw it }
					}
				}
			}
			Given("a character arc called {string} has been created for {string}") { arcName: String, characterName: String ->
				val character = CharacterDriver.getCharacterByIdentifier(double, characterName)!!
				val scope = ProjectSteps.givenProjectHasBeenOpened(double)
				val controller = scope.get<PlanNewCharacterArcController>()
				interact {
					controller.planCharacterArc(character.id.uuid.toString(), arcName) { throw it }
				}
			}
			Given("the following characters have been created") { table: DataTable ->
				val characterNames = table.asList()

				val scope = ProjectSteps.givenProjectHasBeenOpened(double)
				val controller = scope.get<BuildNewCharacterController>()
				interact {
					characterNames.forEach { characterName ->
						controller.createCharacter(characterName) { throw it }
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
				val controller = scope.get<BuildNewCharacterController>()
				interact {
					controller.createCharacter(characterName) { throw it }
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
			When("a Character called {string} is created for the {string} theme") {
					characterName: String, themeName: String ->

				val scope = ProjectSteps.getProjectScope(double)!!
				val theme = ThemeSteps.getThemeWithName(double, themeName)!!
				val controller = scope.get<BuildNewCharacterController>()
				interact {
					controller.createCharacterAndIncludeInTheme(characterName, theme.id.uuid.toString()) { throw it }
				}
			}

		}
	}

}