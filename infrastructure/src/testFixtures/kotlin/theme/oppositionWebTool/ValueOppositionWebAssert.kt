package com.soyle.stories.desktop.view.theme.oppositionWebTool

import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import org.junit.jupiter.api.Assertions.*

class ValueOppositionWebAssert private constructor(private val valueOppositionWebTool: ValueOppositionWebs) {

    companion object {
        fun assertThat(valueOppositionWebTool: ValueOppositionWebs, assertions: ValueOppositionWebAssert.() -> Unit) {
            ValueOppositionWebAssert(valueOppositionWebTool).assertions()
        }
    }

    private val driver = ValueOppositionWebDriver(valueOppositionWebTool)

    fun doesNotHaveValueWebNamed(valueWebName: String) {
        assertNull(driver.getValueWebItemWithName(valueWebName)) { "Value Opposition Web Tool should not have value web $valueWebName" }
    }

    fun hasValueWebNamed(valueWebName: String) {
        assertNotNull(driver.getValueWebItemWithName(valueWebName)) { "Value Opposition Web Tool should have value web $valueWebName" }
    }

    fun andValueWebContent(assertions: ValueWebContentAssert.() -> Unit)
    {
        ValueWebContentAssert().assertions()
    }

    inner class ValueWebContentAssert internal constructor() {

        fun hasNoOppositionValues() {
            assertTrue(driver.getAllOppositionValueCards().isEmpty()) { "Value Opposition Web Tool should not have any opposition value cards" }
        }


        fun hasOppositionValueNamed(oppositionValueName: String) {
            assertNotNull(driver.getOppositionValueCardWithName(oppositionValueName)) { "Value Opposition Web Tool should have opposition card named $oppositionValueName" }
        }
        fun doesNotHaveOppositionValueNamed(oppositionValueName: String) {
            assertNull(driver.getOppositionValueCardWithName(oppositionValueName)) { "Value Opposition Web Tool should not have opposition card named $oppositionValueName" }
        }

    }

}