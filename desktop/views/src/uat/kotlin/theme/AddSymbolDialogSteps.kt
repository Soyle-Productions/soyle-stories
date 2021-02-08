package com.soyle.stories.theme

import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.di.get
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Theme.oppositionValue.OppositionValue
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialog
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicCharacterList
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicLocationList
import com.soyle.stories.theme.addSymbolDialog.components.SymbolicSymbolList
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import com.soyle.stories.theme.usecases.SymbolItem
import io.cucumber.java8.En
import javafx.beans.binding.When
import javafx.scene.control.Hyperlink
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

class AddSymbolDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest()
    {

        var dialogDataIds: Pair<Theme.Id, OppositionValue.Id>? = null

        fun getOpenDialog(): AddSymbolDialog?
        {
            return listWindows().asSequence()
                .filter { it.isShowing }
                .mapNotNull { it.scene.root.uiComponent<AddSymbolDialog>() }
                .firstOrNull()
        }

        fun openDialog(scope: ProjectScope, themeId: Theme.Id, oppositionId: OppositionValue.Id) {
            dialogDataIds = themeId to oppositionId
            interact {
                scope.get<AddSymbolDialog>().show(themeId.uuid.toString(), oppositionId.uuid.toString())
            }
        }

        fun givenDialogHasBeenOpened(double: SoyleStoriesTestDouble): AddSymbolDialog {
            return getOpenDialog() ?: kotlin.run {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope).first()
                val opposition = valueWeb.oppositions.first()
                openDialog(scope, theme.id, opposition.id)
                getOpenDialog()!!
            }
        }

        val AddSymbolDialog.characterTab: Tab?
            get() = (root as? TabPane)?.tabs?.find { it.content?.uiComponent<SymbolicCharacterList>() != null }
        val AddSymbolDialog.locationTab: Tab?
            get() = (root as? TabPane)?.tabs?.find { it.content?.uiComponent<SymbolicLocationList>() != null }
        val AddSymbolDialog.symbolTab: Tab?
            get() = (root as? TabPane)?.tabs?.find { it.content?.uiComponent<SymbolicSymbolList>() != null }
    }

    init {
        with(en) {
            Given("the Add Symbol to Opposition Dialog has been opened") {
                givenDialogHasBeenOpened(double)
            }
            Given("the Create Symbol Dialog has been opened from the Add Symbol to Opposition Dialog") {
                val dialog = givenDialogHasBeenOpened(double)
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val button = from(symbolList.root).lookup(".hyperlink").query<Hyperlink>()
                interact {
                    button.fire()
                }
                assertNotNull(CreateSymbolDialogSteps.getOpenDialog())
            }

            When("the Add Symbol to Opposition Dialog is opened") {
                val scope = ProjectSteps.getProjectScope(double)!!
                val theme = ThemeSteps.getCreatedThemes(double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope).first()
                val opposition = valueWeb.oppositions.first()
                openDialog(scope, theme.id, opposition.id)
            }
            When("the character {string} is selected in the Add Symbol to Opposition Dialog") { characterName: String ->
                val dialog = getOpenDialog()!!
                val characterList = dialog.characterTab!!.content.uiComponent<SymbolicCharacterList>()!!
                val listView = characterList.root as ListView<CharacterItemViewModel>
                val item = listView.items.find { it.characterName == characterName }!!
                interact {
                    listView.selectionModel.select(item)
                }
            }
            When("the location {string} is selected in the Add Symbol to Opposition Dialog") { locationName: String ->
                val dialog = getOpenDialog()!!
                val locationList = dialog.locationTab!!.content.uiComponent<SymbolicLocationList>()!!
                val listView = locationList.root as ListView<LocationItemViewModel>
                val item = listView.items.find { it.name == locationName }!!
                interact {
                    listView.selectionModel.select(item)
                }
            }
            When("the symbol {string} is selected in the Add Symbol to Opposition Dialog") { symbolName: String ->
                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                val item = listView.items.find { it.symbolName == symbolName }!!
                interact {
                    listView.selectionModel.select(item)
                }
            }
            When("the Create Symbol button is selected in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val button = from(symbolList.root).lookup(".hyperlink").query<Hyperlink>()
                interact {
                    button.fire()
                }
            }


            Then("the Add Symbol to Opposition Dialog should be open") {
                assertNotNull(getOpenDialog())
            }
            Then("the Add Symbol to Opposition Dialog should be closed") {
                assertNull(getOpenDialog())
            }
            Then("no characters should be listed in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val characterList = dialog.characterTab!!.content.uiComponent<SymbolicCharacterList>()!!
                val listView = characterList.root as ListView<*>
                assertTrue(listView.items.isEmpty())
            }
            Then("the character tab in the Add Symbol to Opposition Dialog should be disabled") {
                val dialog = getOpenDialog()!!
                val tab = dialog.characterTab!!
                assertTrue(tab.isDisable)
            }
            Then("the character {string} should be listed in the Add Symbol to Opposition Dialog") { characterName: String ->

                val dialog = getOpenDialog()!!
                val characterList = dialog.characterTab!!.content.uiComponent<SymbolicCharacterList>()!!
                val listView = characterList.root as ListView<*>
                assertNotNull(
                    listView.items.filterIsInstance<CharacterItemViewModel>().find { it.characterName == characterName }
                )
            }
            Then("the Add Symbol to Opposition Dialog should show the character {string} renamed to {string}") {
                ogName: String, newName: String ->

                val characterId = CharacterDriver.getCharacterIdByIdentifier(double, ogName)!!
                val dialog = getOpenDialog()!!
                val characterList = dialog.characterTab!!.content.uiComponent<SymbolicCharacterList>()!!
                val listView = characterList.root as ListView<*>
                val item = listView.items.filterIsInstance<CharacterItemViewModel>().find { it.characterId == characterId.uuid.toString() }!!
                assertEquals(newName, item.characterName)
            }
            Then("the Add Symbol to Opposition Dialog should no longer list the character {string}") {
                    characterName: String ->

                val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterName)!!
                val dialog = getOpenDialog()!!
                val characterList = dialog.characterTab!!.content.uiComponent<SymbolicCharacterList>()!!
                val listView = characterList.root as ListView<*>
                assertNull(listView.items.filterIsInstance<CharacterItemViewModel>().find { it.characterId == characterId.uuid.toString() })
            }
            Then("no locations should be listed in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val locationList = dialog.locationTab!!.content.uiComponent<SymbolicLocationList>()!!
                val listView = locationList.root as ListView<*>
                assertTrue(listView.items.isEmpty())
            }
            Then("the location tab in the Add Symbol to Opposition Dialog should be disabled") {
                val dialog = getOpenDialog()!!
                val tab = dialog.locationTab!!
                assertTrue(tab.isDisable)
            }
            Then("all locations should be listed in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val locationList = dialog.locationTab!!.content.uiComponent<SymbolicLocationList>()!!
                val listView = locationList.root as ListView<*>
                assertEquals(
                    LocationSteps.getLocationsCreated(double).size,
                    listView.items.size
                )
            }
            Then("the Add Symbol to Opposition Dialog should show the location {string} renamed to {string}") {
                    ogName: String, newName: String ->

                val dialog = getOpenDialog()!!
                val locationList = dialog.locationTab!!.content.uiComponent<SymbolicLocationList>()!!
                val listView = locationList.root as ListView<*>
                assertNotNull(listView.items.filterIsInstance<LocationItemViewModel>().find { it.name == newName })
            }
            Then("the Add Symbol to Opposition Dialog should no longer list the location {string}") {
                locationName: String ->

                val dialog = getOpenDialog()!!
                val locationList = dialog.locationTab!!.content.uiComponent<SymbolicLocationList>()!!
                val listView = locationList.root as ListView<*>
                assertNull(listView.items.filterIsInstance<LocationItemViewModel>().find { it.name == locationName })
            }
            Then("no symbols should be listed in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                assertTrue(listView.items.isEmpty())
            }
            Then("the symbol tab in the Add Symbol to Opposition Dialog should not be disabled") {
                val dialog = getOpenDialog()!!
                val tab = dialog.symbolTab!!
                assertFalse(tab.isDisable)
            }
            Then("all symbols in this theme should be listed in the Add Symbol to Opposition Dialog") {
                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                assertEquals(
                    ThemeSteps.getCreatedThemes(double).first().symbols.size,
                    listView.items.size
                )
            }
            Then("the symbol {string} should not be listed in the Add Symbol to Opposition Dialog") { symbolName: String ->
                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                assertNull(listView.items.find { it.symbolName == symbolName })
            }
            Then("the Add Symbol to Opposition Dialog should show the symbol {string} renamed to {string}") {
                    ogName: String, newName: String ->

                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                assertNotNull(listView.items.find { it.symbolName == newName })
            }
            Then("the Add Symbol to Opposition Dialog should no longer list the Symbol {string}") { symbolName: String ->

                val dialog = getOpenDialog()!!
                val symbolList = dialog.symbolTab!!.content.uiComponent<SymbolicSymbolList>()!!
                val listView = from(symbolList.root).lookup(".list-view").queryListView<SymbolListItemViewModel>()
                assertNull(listView.items.find { it.symbolName == symbolName })
            }
        }
    }

}