package com.soyle.stories.storyevent

import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import javafx.event.ActionEvent
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class StoryEventSteps(en: En, double: SoyleStoriesTestDouble) : ApplicationTest() {

	private var createdStoryEvent: StoryEvent? = null

	private fun firstStoryEventId(double: SoyleStoriesTestDouble) =
	  StoryEventsDriver.storyEventCreated().get(double)?.id

	init {
		with(en) {

			Given("the Create Story Event Dialog has been opened") {
				CreateStoryEventDialogDriver.openDialog.given(double)
			}
			Given("the Create Story Event Dialog Name input has an invalid Story Event Name") {
				CreateStoryEventDialogDriver.invalidName.given(double)
			}
			Given("the Create Story Event Dialog Name input has a valid Story Event Name") {
				CreateStoryEventDialogDriver.validName.given(double)
			}
			Given("The Story Event List Tool has been opened") {
				StoryEventListToolDriver.openTool.given(double)
			}
			Given("A Story Event has been created") {
				StoryEventsDriver.storyEventsCreated(1).given(double)
			}
			Given("the Story Event right-click menu has been opened in the Story Event List Tool") {
				StoryEventListToolDriver.openRightClickMenu.given(double)
			}
			Given("The Story Event List Tool tab has been selected") {
				StoryEventListToolDriver.tabSelected.given(double)
			}
			Given("{int} Story Events have been created") { count: Int ->
				StoryEventsDriver.storyEventsCreated(count).given(double)
			}
			Given("a Story Event has been created") {
				StoryEventsDriver.storyEventCreated().given(double)
			}
			Given("a Story Event has been selected") {
				StoryEventListToolDriver.selectedItem.given(double)
			}
			Given("the Story Event Details Tool has been opened") {
				StoryEventDetailsToolDriver.openToolWith(
				  firstStoryEventId(double)!!
				).given(double)
			}
			Given("the Story Events Details Tool Location dropdown menu has been opened") {
				StoryEventDetailsToolDriver.visibleLocationDropDownMenu(
				  firstStoryEventId(double)!!
				).given(double)
			}
			Given("the Story Event Details Character Selection dropdown menu has been opened") {
				StoryEventDetailsToolDriver.visibleCharacterDropDownMenu(
				  firstStoryEventId(double)!!
				).given(double)
			}


			When("The Story Event List Tool is opened") {
				if (StoryEventListToolDriver.openTool.check(double)) {
					StoryEventListToolDriver.openTool.whenSet(double)
				}
				StoryEventListToolDriver.openTool.whenSet(double)
			}
			When("User clicks the center create new story event button") {
				interact {
					StoryEventListToolDriver.centerButton.get(double)!!.onAction.handle(ActionEvent())
				}
			}
			When("User clicks the bottom create new Story Event button") {
				interact {
					StoryEventListToolDriver.actionBarButton("create").get(double)!!.onAction.handle(ActionEvent())
				}
			}
			When("the {string} Story Event right-click menu option is selected in the Story Event List Tool") { option: String ->
				StoryEventListToolDriver.rightClickMenuOption(option).whenSet(double)
			}
			When("A new Story Event is created without a relative Story Event") {
				val existing = StoryEventsDriver.storyEventsCreated.get(double)?.map { it.id }?.toSet() ?: emptySet()
				StoryEventsDriver.storyEventCreated().whenSet(double)
				createdStoryEvent = StoryEventsDriver.storyEventsCreated.get(double)?.filterNot { it.id in existing }?.firstOrNull()
			}
			When("A new Story Event is created before a relative Story Event") {
				val existing = StoryEventsDriver.storyEventsCreated.get(double)?.map { it.id }?.toSet() ?: emptySet()
				StoryEventsDriver.storyEventCreatedBefore(StoryEventsDriver.storyEventCreated().get(double)!!.id.uuid.toString()).whenSet(double)
				createdStoryEvent = StoryEventsDriver.storyEventsCreated.get(double)?.filterNot { it.id in existing }?.firstOrNull()
			}
			When("A new Story Event is created after the first Story Event") {
				val existing = StoryEventsDriver.storyEventsCreated.get(double)?.map { it.id }?.toSet() ?: emptySet()
				StoryEventsDriver.storyEventCreatedAfter(StoryEventsDriver.storyEventCreated().get(double)!!.id.uuid.toString()).whenSet(double)
				createdStoryEvent = StoryEventsDriver.storyEventsCreated.get(double)?.filterNot { it.id in existing }?.firstOrNull()
			}
			When("the user clicks the Story Event List Tool delete button") {
				interact {
					StoryEventListToolDriver.actionBarButton("delete").get(double)!!.onAction.handle(ActionEvent())
				}
			}
			When("the Story Event Details Tool is opened") {
				StoryEventDetailsToolDriver.openToolWith(firstStoryEventId(double)!!).whenSet(double)
			}
			When("the Story Event Details Tool Location dropdown is clicked") {
				interact {
					StoryEventDetailsToolDriver.enabledLocationDropDown(
					  firstStoryEventId(double)!!
					).get(double)!!.onAction!!.handle(ActionEvent())
				}
			}
			When("a Location in the Story Events Details Tool Location dropdown menu is selected") {
				interact {
					StoryEventDetailsToolDriver.locationDropDownMenuItems(
					  firstStoryEventId(double)!!
					).get(double)!!.first().fire()
				}
			}
			When("the user clicks outside the Story Events Details Tool Location dropdown menu") {
				interact {
					clickOn(StoryEventDetailsToolDriver.openToolWith(
					  firstStoryEventId(double)!!
					).get(double)!!.owningTab?.tabPane, MouseButton.PRIMARY)
				}
			}
			When("the Story Event Details Character Selection dropdown is clicked") {
				interact {
					StoryEventDetailsToolDriver.characterDropDown(
					  firstStoryEventId(double)!!
					).whenSet(double)
				}
			}
			When("a Character in the Story Event Details Character Selection dropdown menu is selected") {
				interact {
					StoryEventDetailsToolDriver.characterDropDownItems(
					  firstStoryEventId(double)!!
					).get(double)!!.first().fire()
				}
			}


			Then("an error message should be displayed in the Create Story Event Dialog") {
				assertTrue(CreateStoryEventDialogDriver.errorMessage.check(double))
			}
			Then("The create new Story Event dialog should be open") {
				assertTrue(CreateStoryEventDialogDriver.openDialog.check(double))
			}
			Then("the Create Story Event Dialog should be closed") {
				assertFalse(CreateStoryEventDialogDriver.openDialog.check(double))
			}
			Then("a new Story Event should be created") {
				assertTrue(StoryEventsDriver.storyEventsCreated(1).check(double))
			}
			Then("The Story Event List Tool should show a special empty message") {
				assertTrue(StoryEventListToolDriver.visibleEmptyDisplay.check(double))
			}
			Then("The Story Event List Tool should show the new Story Event") {
				assertTrue(StoryEventListToolDriver.isShowingStoryEvent(createdStoryEvent!!).check(double))
			}
			Then("the new Story Event should be at the end of the Story Event List Tool") {
				assertTrue(StoryEventListToolDriver.isShowingStoryEventAtEnd(createdStoryEvent!!).check(double))
			}
			Then("the new Story Event should be listed before the relative Story Event in the Story Event List Tool") {
				assertTrue(StoryEventListToolDriver.isShowingStoryEventBefore(createdStoryEvent!!, createdStoryEvent!!.nextStoryEventId!!).check(double))
			}
			Then("the new Story Event should be listed after the first Story Event in the Story Event List Tool") {
				assertTrue(StoryEventListToolDriver.isShowingStoryEventAfter(createdStoryEvent!!, createdStoryEvent!!.previousStoryEventId!!).check(double))
			}
			Then("The Story Event List Tool should show all {int} story events") { count: Int ->
				assertEquals(count, StoryEventListToolDriver.listedItems.get(double)!!.size)
			}
			Then("the Story Events Details Tool Location dropdown in the should be disabled") {
				assertTrue(StoryEventDetailsToolDriver.disabledLocationDropDown(
				  firstStoryEventId(double)!!).check(double))
			}
			Then("all Locations should be listed in the Story Events Details Tool Location dropdown menu") {
				assertEquals(
				  LocationSteps.getNumberOfLocationsCreated(double),
				  StoryEventDetailsToolDriver.locationDropDownMenuItemCount(
					firstStoryEventId(double)!!, double
				  )
				)
			}
			Then("the Story Events Details Tool Location dropdown should show the selected Location name") {
				assertEquals(
				  LocationSteps.getLocationsCreated(double).first().name,
				  StoryEventDetailsToolDriver.enabledLocationDropDown(
					firstStoryEventId(double)!!
				  ).get(double)!!.text
				)
			}
			Then("the Story Events Details Tool Location dropdown menu should be closed") {
				assertFalse(StoryEventDetailsToolDriver.visibleLocationDropDownMenu(
				  firstStoryEventId(double)!!
				).check(double))
			}
			Then("the Story Event Details Character Selection dropdown should be disabled") {
				assertTrue(StoryEventDetailsToolDriver.disabledCharacterDropDown(
				  firstStoryEventId(double)!!
				).check(double))
			}
			Then("all Characters should be listed in the Story Event Details Character Selection dropdown menu") {
				assertEquals(
				  CharacterDriver.getNumberOfCharactersCreated(double),
				  StoryEventDetailsToolDriver.characterDropDownItemCount(
					firstStoryEventId(double)!!, double
				  )
				)
			}
			Then("the selected Character should be shown in place of the Story Event Details Character Selection dropdown") {
				val firstCharacterName = CharacterDriver.getCharactersCreated(double).first().name
				val includedCharacters = StoryEventDetailsToolDriver.includedCharacters(firstStoryEventId(double)!!).get(double)!!
				assertNotNull(includedCharacters.find {
					it.text == firstCharacterName
				})
			}
			Then("the Story Event Details Character Selection dropdown should be below the list of included characters") {

			}

		}
	}

}