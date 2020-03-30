package com.soyle.stories.layout

import io.cucumber.java8.En
import io.cucumber.java8.PendingException

class LayoutSteps : En {

    init {

        Given("The layout has been in {string}") { state: String ->
            val layoutState = LayoutState.valueOf(state)
            throw PendingException()
        }

        Then("The default tool group should be open") { throw PendingException() }

        Then("The helper tips tool should be open") { throw PendingException() }

        Then("The character list tool should be open") { throw PendingException() }

        Then("The layout should be in {string}") { state: String ->
            val layoutState = LayoutState.valueOf(state)
            throw PendingException()
        }
    }

}