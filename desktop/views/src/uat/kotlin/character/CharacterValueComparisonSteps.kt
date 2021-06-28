package com.soyle.stories.character

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.di.get
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.ThemeSteps
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparison
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonScope
import com.soyle.stories.theme.repositories.CharacterArcRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.control.Labeled
import javafx.scene.control.MenuButton
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class CharacterValueComparisonSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

    }

    init {
        with(en) {
            Given("the Character Value Comparison tool has been opened for the {string} theme") {
                    themeName: String ->

                val theme = ThemeSteps.givenAThemeHasBeenCreatedWithTheName(double, themeName)
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val controller = scope.get<OpenToolController>()
                interact {
                    controller.openCharacterValueComparison(theme.id.uuid.toString())
                }
            }
            Given("the author has indicated they want to include a character") {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .first()
                    .get<CharacterValueComparison>()
                val addCharacterBtn = from(tool.root).lookup(".${ComponentsStyles.buttonCombo.name}").queryAll<MenuButton>()
                    .find { it.text == "Add Character ..." }!!
                interact {
                    addCharacterBtn.show()
                }
            }
            Given("the author has indicated they want to create a character to include in the {string} theme") {
                themeName: String ->

                val theme = ThemeSteps.givenAThemeHasBeenCreatedWithTheName(double, themeName)
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val addCharacterBtn = from(tool.root).lookup(".${ComponentsStyles.buttonCombo.name}").queryAll<MenuButton>()
                    .find { it.text == "Add Character ..." }!!
                interact {
                    addCharacterBtn.show()
                }
            }


            When("the Character Value Comparison tool is opened for {string} s {string} character arc") {
                characterName: String, arcName: String ->

                val character = CharacterDriver.getCharacterByIdentifier(double, characterName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val repository = scope.get<CharacterArcRepository>()
                val arc = runBlocking {
                    repository.listCharacterArcsForCharacter(character.id)!!.find { it.name == arcName }!!
                }
                val controller = scope.get<OpenToolController>()
                interact {
                    controller.openCharacterValueComparison(arc.themeId.uuid.toString())
                }
            }
            When("the Character Value Comparison tool is opened for the {string} theme") {
                themeName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val controller = scope.get<OpenToolController>()
                interact {
                    controller.openCharacterValueComparison(theme.id.uuid.toString())
                }
            }
            When("the author indicates they want to include a character") {
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .first()
                    .get<CharacterValueComparison>()
                val addCharacterBtn = from(tool.root).lookup(".${ComponentsStyles.buttonCombo.name}").queryAll<MenuButton>()
                    .find { it.text == "Add Character ..." }!!
                interact {
                    addCharacterBtn.show()
                }
            }


            Then("the {string} Character Value Comparison tool should list the character {string}") {
                themeName: String, characterName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                from(tool.root).lookup(characterName).query<Labeled>()
            }
            Then("the {string} Character Value Comparison tool should list no characters") {
                    themeName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val charactersPane = from(tool.root).lookup("#character-values").queryParent()
                assertTrue(charactersPane.childrenUnmodifiable.isEmpty())
            }
            Then("the {string} Character Value Comparison tool should list the following characters") {
                themeName: String, table: DataTable ->
                val characterNames = table.asList()

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val charactersPane = from(tool.root).lookup("#character-values").queryParent()
                assertEquals(characterNames.size, charactersPane.childrenUnmodifiable.size)
                characterNames.forEach {
                    from(charactersPane).lookup(it).queryLabeled()
                }
            }
            Then("the {string} Character Value Comparison tool should not list the character {string}") {
                themeName: String, characterName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val charactersPane = from(tool.root).lookup("#character-values").queryParent()
                assertNull(from(charactersPane).lookup(characterName).queryAll<Labeled>().firstOrNull())
            }
            Then("the character {string} should be available to include in the {string} theme") {
                characterName: String, themeName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val addCharacterBtn = from(tool.root).lookup(".${ComponentsStyles.buttonCombo.name}").queryAll<MenuButton>()
                    .find { it.text == "Add Character ..." }!!
                assertNotNull(addCharacterBtn.items.find { it.text == characterName })
            }
            Then("the following characters should not be available to include in the {string} theme") {
                    themeName: String, table: DataTable ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val scope = ProjectSteps.getProjectScope(double)!!
                val tool = scope.toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
                    .find { it.type.themeId == theme.id.uuid }!!
                    .get<CharacterValueComparison>()
                val addCharacterBtn = from(tool.root).lookup(".${ComponentsStyles.buttonCombo.name}").queryAll<MenuButton>()
                    .find { it.text == "Add Character ..." }!!
                table.asList().forEach { characterName ->
                    assertNull(addCharacterBtn.items.find { it.text == characterName })
                }
            }
        }
    }

}