package com.soyle.stories.storyevent

import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java.PendingException
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class StoryEventSteps(en: En, double: SoyleStoriesTestDouble) {

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
				throw PendingException()
			}
			Given("A Story Event has been created") {
				StoryEventsDriver.storyEventsCreated(1).given(double)
			}
			Given("the Story Event right-click menu has been opened in the Story Event List Tool") {
				throw PendingException()
			}
			Given("The Story Event List Tool tab has been selected") {
				throw PendingException()
			}
			Given("{int} Story Events have been created") { count: Int ->
				StoryEventsDriver.storyEventsCreated(count).given(double)
			}


			When("The Story Event List Tool is opened") {
				throw PendingException()
			}
			When("User clicks the center create new story event button") {
				throw PendingException()
			}
			When("User clicks the bottom create new Story Event button") {
				throw PendingException()
			}
			When("the {string} Story Event right-click menu option is selected in the Story Event List Tool") { option: String ->
				throw PendingException()
			}
			When("A new Story Event is created without a relative Story Event") {
				throw PendingException()
			}
			When("A new Story Event is created before a relative Story Event") {
				throw PendingException()
			}
			When("A new Story Event is created after the first Story Event") {
				throw PendingException()
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
				throw PendingException()
			}
			Then("The Story Event List Tool should show the new Story Event") {
				throw PendingException()
			}
			Then("the new Story Event should be at the end of the Story Event List Tool") {
				throw PendingException()
			}
			Then("the new Story Event should be listed before the relative Story Event in the Story Event List Tool") {
				throw PendingException()
			}
			Then("the new Story Event should be listed after the first Story Event in the Story Event List Tool") {
				throw PendingException()
			}
		}
	}

}