package com.soyle.stories.theme

import com.soyle.stories.common.components.ComponentsStyles.Companion.cardBody
import com.soyle.stories.common.components.PopOutEditBox
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.di.get
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.themeOppositionWebs.Styles
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import javafx.scene.layout.FlowPane
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.contextmenu
import tornadofx.decorators

class ValueOppositionWebSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        var renameRequest: Pair<Any, String>? = null

        private val validValueWebName = "Valid Value Web Name"
        private val validOppositionName = "Valid Opposition Name"

        fun getOpenTool(double: SoyleStoriesTestDouble): ValueOppositionWebs?
        {
            val projectScope = ProjectSteps.getProjectScope(double) ?: return null
            val scope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().firstOrNull() ?: return null
            return findComponentsInScope<ValueOppositionWebs>(scope).firstOrNull()
        }

        fun openTool(projectScope: ProjectScope, themeId: String)
        {
            val controller = projectScope.get<OpenToolController>()
            interact {
                controller.openValueOppositionWeb(themeId)
            }
        }

        fun getOpenToolForTheme(double: SoyleStoriesTestDouble, theme: Theme): ValueOppositionWebs?
        {
            val projectScope = ProjectSteps.getProjectScope(double) ?: return null
            val scope = projectScope.toolScopes.asSequence()
                .filterIsInstance<ValueOppositionWebsScope>()
                .find { it.themeId == theme.id.uuid } ?: return null
            return findComponentsInScope<ValueOppositionWebs>(scope).firstOrNull()
        }

        fun givenToolHasBeenOpened(double: SoyleStoriesTestDouble): ValueOppositionWebs
        {
            return getOpenTool(double) ?: run {
                openTool(
                    ProjectSteps.givenProjectHasBeenOpened(double),
                    ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first().id.uuid.toString()
                )
                getOpenTool(double)!!
            }
        }

        fun givenToolHasBeenOpenedForTheme(double: SoyleStoriesTestDouble, theme: Theme): ValueOppositionWebs
        {
            return getOpenToolForTheme(double, theme) ?: run {
                openTool(
                    ProjectSteps.givenProjectHasBeenOpened(double),
                    theme.id.uuid.toString()
                )
                getOpenToolForTheme(double, theme)!!
            }
        }

        fun getOpenPopOutEditBoxForNode(node: Node): PopOutEditBox?
        {
            return listWindows().asSequence().filterIsInstance<PopOutEditBox>()
                .filter { it.isShowing }
                .find { it.node == node }
        }

        fun givenPopOutEditBoxHasBeenOpenedForNode(node: Node): PopOutEditBox
        {
            return getOpenPopOutEditBoxForNode(node) ?: run {
                interact {
                    node.popOutEditBox?.popup()
                }
                getOpenPopOutEditBoxForNode(node)!!
            }
        }
    }

    private var cardCountSnapshot: Int = -1

    init {
        with(en) {

            Given("the Value Opposition Web Tool has been opened") {
                givenToolHasBeenOpened(double)
            }
            Given("the Value Opposition Web Tool has been opened for the theme {string}") { themeName: String ->
                val theme = ThemeSteps.givenAThemeHasBeenCreatedWithTheName(double, themeName)
                givenToolHasBeenOpenedForTheme(double, theme)
            }
            Given("a value web has been created for the theme open in the Value Opposition Web Tool") {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope)
            }
            Given("{int} value oppositions have been created for the value web") { count: Int ->
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope).first()
                ThemeSteps.givenANumberOfOppositionsHaveBeenCreated(count, valueWeb.id.uuid.toString(), scope)
            }
            Given("a value opposition has been created for the value web") {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope).first()
                ThemeSteps.givenANumberOfOppositionsHaveBeenCreated(1, valueWeb.id.uuid.toString(), scope)
            }
            Given("the value web in the Value Opposition Web Tool has been selected") {
                val tool = givenToolHasBeenOpened(double)
                val valueWebLink = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").query<Hyperlink>()
                interact {
                    valueWebLink.fire()
                }
            }
            Given("the {string} value web has been selected in the {string} Value Opposition Web Tool") {
                valueWebName: String, themeName: String ->

                val theme = ThemeSteps.givenAThemeHasBeenCreatedWithTheName(double, themeName)
                val tool = givenToolHasBeenOpenedForTheme(double, theme)
                val valueWebLinks = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").queryAll<Hyperlink>()
                val valueWebLink = valueWebLinks.find { it.text == valueWebName }!!
                interact {
                    valueWebLink.fire()
                }
            }
            Given("all value oppositions have been removed from the value web") {
                TODO()
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), scope).first()
                //ThemeSteps.givenTheNumberOfOppositionsHasBeenReducedTo(0)
            }
            Given("the value opposition is being renamed") {
                val tool = givenToolHasBeenOpened(double)
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val card = oppositionCards.first()
                val cardName = from(card).lookup(".hyperlink").query<Hyperlink>()
                givenPopOutEditBoxHasBeenOpenedForNode(cardName)
            }
            Given("a valid value opposition name has been entered in the value opposition rename text box") {
                val tool = givenToolHasBeenOpened(double)
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val card = oppositionCards.first()
                val cardName = from(card).lookup(".hyperlink").query<Hyperlink>()
                val popOutEditBox = givenPopOutEditBoxHasBeenOpenedForNode(cardName)
                interact {
                    popOutEditBox.textInput.text = validOppositionName
                }
            }
            Given("an invalid value opposition name has been entered in the value opposition rename text box") {
                val tool = givenToolHasBeenOpened(double)
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val card = oppositionCards.first()
                val cardName = from(card).lookup(".hyperlink").query<Hyperlink>()
                val popOutEditBox = givenPopOutEditBoxHasBeenOpenedForNode(cardName)
                interact {
                    popOutEditBox.textInput.text = ""
                }
            }
            Given("the value web is being renamed") {
                val tool = givenToolHasBeenOpened(double)
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                givenPopOutEditBoxHasBeenOpenedForNode(valueWebName)
            }
            Given("a valid value web name has been entered in the value web rename text box") {
                val tool = givenToolHasBeenOpened(double)
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                val popOutEditBox = givenPopOutEditBoxHasBeenOpenedForNode(valueWebName)
                interact {
                    popOutEditBox.textInput.text = validValueWebName
                }
            }
            Given("an invalid value web name has been entered in the value web rename text box") {
                val tool = givenToolHasBeenOpened(double)
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                val popOutEditBox = givenPopOutEditBoxHasBeenOpenedForNode(valueWebName)
                interact {
                    popOutEditBox.textInput.text = ""
                }
            }

            When("the Value Opposition Web Tool is opened") {
                openTool(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
                )
            }
            When("the Value Opposition Web Tool Create Value Web button is selected") {
                val tool = getOpenTool(double)!!
                val button = from(tool.root).lookup(".create-value-web-button").queryButton()
                interact {
                    button.fire()
                }
            }
            When("a value web in the Value Opposition Web Tool is selected") {
                val tool = getOpenTool(double)!!
                val valueWebLink = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").query<Hyperlink>()
                interact {
                    valueWebLink.fire()
                }
            }
            When("the Value Opposition Web Tool Create Value Opposition button is selected") {
                val tool = getOpenTool(double)!!
                val btn = from(tool.root).lookup("Add Opposition").query<Button>()
                cardCountSnapshot = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>().size
                interact {
                    btn.fire()
                }
            }
            When("the value opposition name is selected") {
                val tool = getOpenTool(double)!!
                val cardName = from(tool.root).lookup(".${Styles.oppositionCard.name} .hyperlink").query<Hyperlink>()
                interact {
                    cardName.fire()
                }
            }
            When("the value opposition rename is cancelled by Pressing Escape") {
                interact {
                    press(KeyCode.ESCAPE).release(KeyCode.ESCAPE)
                }
            }
            When("the value opposition rename is cancelled by Clicking Away") {
                val popOutEditBox = listWindows().asSequence().filterIsInstance<PopOutEditBox>()
                    .find { it.isShowing }!!
                interact {
                    // have to use "complete" instead of clicking on another node/stage because headless testfx doesn't
                    // lose focus when another stage is focused.
                    popOutEditBox.complete()
                }
            }
            When("the value opposition rename is committed by Pressing Enter") {
                interact {
                    press(KeyCode.ENTER).release(KeyCode.ENTER)
                }
            }
            When("the value opposition rename is committed by Clicking Away") {
                val popOutEditBox = listWindows().asSequence().filterIsInstance<PopOutEditBox>()
                    .find { it.isShowing }!!
                interact {
                    // have to use "complete" instead of clicking on another node/stage because headless testfx doesn't
                    // lose focus when another stage is focused.
                    popOutEditBox.complete()
                }
            }
            When("the value web name is selected") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                interact {
                    valueWebName.fire()
                }
            }
            When("the value web menu button {string} button is selected in the Value Opposition Web Tool") { button: String ->
                val tool = getOpenTool(double)!!
                val menuButton = from(tool.root).lookup(".menu-button").query<MenuButton>()
                val items = menuButton.items.toList()
                val item = items.find { it.text == button }!!
                interact { item.fire() }
            }
            When("the value web rename is cancelled by Pressing Escape") {
                interact {
                    press(KeyCode.ESCAPE).release(KeyCode.ESCAPE)
                }
            }
            When("the value web rename is committed by Pressing Enter") {
                interact {
                    press(KeyCode.ENTER).release(KeyCode.ENTER)
                }
            }
            When("the {string} value web is selected in the {string} Value Opposition Web Tool") {
                valueWebName: String, themeName: String ->

                val theme = ThemeSteps.givenAThemeHasBeenCreatedWithTheName(double, themeName)
                val tool = givenToolHasBeenOpenedForTheme(double, theme)
                val valueWebLinks = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").queryAll<Hyperlink>()
                val valueWebLink = valueWebLinks.find { it.text == valueWebName }!!
                interact {
                    valueWebLink.fire()
                }
            }
            When("the {string} button is selected on the first value opposition in the {string} value web") {
                buttonName: String, valueWebName: String ->
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val firstCard = oppositionCards.first()
                val button = from(firstCard).lookup(buttonName).query<Button>()
                interact {
                    button.fire()
                }
            }
            When("the value opposition {string} button is selected") { buttonName: String ->
                val tool = getOpenTool(double)!!
                val card = from(tool.root).lookup(".${Styles.oppositionCard.name}").query<Node>()
                val button = from(card).lookup(buttonName).queryButton()
                interact {
                    button.fire()
                }
            }
            When("the symbolic item {string} is removed from the {string} theme's {string} value web's first opposition") {
                    itemName: String, themeName: String, valueWebName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val tool = givenToolHasBeenOpenedForTheme(double, theme)
                val valueWebLinks = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").queryAll<Hyperlink>()
                val valueWebLink = valueWebLinks.find { it.text == valueWebName }!!
                interact {
                    valueWebLink.fire()
                }
                val card = from(tool.root).lookup(".${Styles.oppositionCard.name}").query<Node>()
                val chips = from(card).lookup(".${cardBody.name} .chips").query<FlowPane>()
                val chipBtn = chips.children.asSequence()
                    .filter { from(it).lookup(".label").queryAll<Label>().any { it.text == itemName } }
                    .map { from(it).lookup(".button").query<Button>() }
                    .first()
                interact { chipBtn.fire() }
            }


            Then("the Value Web Tool should be open") {
                assertNotNull(getOpenTool(double))
            }
            Then("the Value Opposition Web Tool should show a special empty message") {
                val tool = getOpenTool(double)!!
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                val populated = from(tool.root).lookup(".populated").query<Parent>()
                assertTrue(emptyDisplay.visibleProperty().get())
                assertFalse(populated.visibleProperty().get())
            }
            Then("the Value Opposition Web Tool should list all {int} value webs") { count: Int ->
                val tool = getOpenTool(double)!!
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                assertFalse(emptyDisplay.visibleProperty().get())
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertEquals(count, valueWebList.childrenUnmodifiable.size) {
                    "Wrong number of children in value web list.\n" +
                            "Children: ${valueWebList.childrenUnmodifiable}\n" +
                            "Model: ${tool.scope.get<ValueOppositionWebsModel>().valueWebs}"
                }
                val populated = from(tool.root).lookup(".populated").query<Parent>()
                assertTrue(populated.visibleProperty().get())
            }
            Then("the Value Opposition Web Tool should show the created value web") {
                val tool = getOpenTool(double)!!
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertEquals(ThemeSteps.getCreatedThemes(double).first().valueWebs.size, valueWebList.childrenUnmodifiable.size) {
                    "Wrong number of children in value web list.\n" +
                            "Children: ${valueWebList.childrenUnmodifiable}\n" +
                            "Model: ${tool.scope.get<ValueOppositionWebsModel>().valueWebs}"
                }
            }
            Then("the Value Opposition Web Tool Opposition Web should list all {int} value oppositions") { count: Int ->
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                assertEquals(count, oppositionCards.size)
            }
            Then("a new value opposition should be listed in the Value Opposition Web Tool Opposition Web") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                assertEquals(cardCountSnapshot + 1, oppositionCards.size)
            }
            Then("the value opposition name should be a text box") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                getOpenPopOutEditBoxForNode(cardName)!!
            }
            Then("the value opposition name should not be a text box") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                assertNull(getOpenPopOutEditBoxForNode(cardName))
            }
            Then("the value opposition name text box should be focused") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(cardName)!!
                assertTrue(popout.textInput.isFocused)
            }
            Then("the text in the value opposition name text box should be selected") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(cardName)!!
                assertEquals(popout.textInput.text, popout.textInput.selectedText)
            }
            Then("the value opposition rename text box should show an error message") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(cardName)!!
                popout.textInput.decorators.single()
            }
            Then("the value opposition name should show an error message") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                cardName.decorators.single()
            }
            Then("the value opposition should be renamed") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                assertEquals(validOppositionName, cardName.text)
            }
            Then("the value opposition should not be renamed") {
                val tool = getOpenTool(double)!!
                val oppositionCards = from(tool.root).lookup(".${Styles.oppositionCard.name}").queryAll<Node>()
                val lastCard = oppositionCards.last()
                val cardName = from(lastCard).lookup(".hyperlink").query<Hyperlink>()
                assertNotEquals(validOppositionName, cardName.text)
            }
            Then("the value web name should not be a text box") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                assertNull(getOpenPopOutEditBoxForNode(valueWebName))
            }
            Then("the value web name should be a text box") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                getOpenPopOutEditBoxForNode(valueWebName)!!
            }
            Then("the value web name text box should be focused") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(valueWebName)!!
                assertTrue(popout.textInput.isFocused)
            }
            Then("the text in the value web name text box should be selected") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(valueWebName)!!
                assertEquals(popout.textInput.text, popout.textInput.selectedText)
            }
            Then("the value web should be renamed") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                assertEquals(validValueWebName, valueWebName.text)
            }
            Then("the value web should not be renamed") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                assertNotEquals(validValueWebName, valueWebName.text)
            }
            Then("the value web rename text box should show an error message") {
                val tool = getOpenTool(double)!!
                val valueWebName = from(tool.root).lookup("#ValueWebName").query<Hyperlink>()
                val popout = getOpenPopOutEditBoxForNode(valueWebName)!!
                popout.textInput.decorators.single()
            }
            Then("the {string} Value Opposition Web Tool should not list the {string} value web") {
                themeName: String, valueWebName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val tool = getOpenToolForTheme(double, theme)!!
                val valueWebLinks = from(tool.root).lookup(".${Styles.valueWebList.name} .hyperlink").queryAll<Hyperlink>()
                assertNull(valueWebLinks.find { it.text == valueWebName })
            }
            Then("the {string} Value Opposition Web Tool should have no value web selected") {
                themeName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val tool = getOpenToolForTheme(double, theme)!!
                assertNull(tool.scope.get<ValueOppositionWebsModel>().selectedValueWeb.value)
            }
            Then("the {string} Value Opposition Web Tool Opposition Web should show a special empty message") {
                    themeName: String ->

                val theme = ThemeSteps.getThemeWithName(double, themeName)!!
                val tool = getOpenToolForTheme(double, theme)!!
                val content = from(tool.root).lookup(".content-pane").query<Parent>()
                val emptyOppositionDisplay = from(content).lookup(".empty-display").query<Node>()
                assertTrue(emptyOppositionDisplay.visibleProperty().get())
            }

        }
    }


}