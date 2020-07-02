package com.soyle.stories.theme

import com.soyle.stories.common.components.PopOutEditBox
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.di.get
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
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextInputControl
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.contextmenu
import tornadofx.decorators

class ValueOppositionWebSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

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
            Then("the Value Opposition Web Tool Opposition Web should show a special empty message") {
                TODO()
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
        }
    }


}