package com.soyle.stories.desktop.view.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.moralArgument.MoralArgumentSectionTypeViewModel
import com.soyle.stories.theme.moralArgument.MoralArgumentView
import javafx.scene.control.*
import org.junit.jupiter.api.Assertions.*
import org.testfx.assertions.api.Assertions
import org.testfx.assertions.api.ButtonAssert
import tornadofx.hasClass

class MoralArgumentViewAssert private constructor(view: MoralArgumentView) {

    companion object {
        fun assertThat(view: MoralArgumentView, assertions: MoralArgumentViewAssert.() -> Unit) {
            MoralArgumentViewAssert(view).assertions()
        }
    }

    private val driver = MoralArgumentViewDriver(view)

    fun andMoralProblemField(assertions: MoralProblemAssert.() -> Unit) {
        MoralProblemAssert().assertions()
    }

    inner class MoralProblemAssert internal constructor() {

        fun hasLabel(expectedLabel: String) {
            assertEquals(expectedLabel, driver.getMoralProblemFieldLabel().text)
        }

        fun hasValue(expectedValue: String) {
            assertEquals(expectedValue, driver.getMoralProblemFieldInput().text)
        }

    }

    fun andThemeLineField(assertions: ThemeLineAssert.() -> Unit) {
        ThemeLineAssert().assertions()
    }

    inner class ThemeLineAssert internal constructor() {

        fun hasLabel(expectedLabel: String) {
            assertEquals(expectedLabel, driver.getThemeLineFieldLabel().text)
        }

        fun hasValue(expectedValue: String) {
            assertEquals(expectedValue, driver.getThemeLineFieldInput().text)
        }
    }

    fun andThematicRevelationField(assertions: ThematicRevelationAssert.() -> Unit) {
        ThematicRevelationAssert().assertions()
    }

    inner class ThematicRevelationAssert internal constructor() {

        fun hasLabel(expectedLabel: String) {
            assertEquals(expectedLabel, driver.getThematicRevelationFieldLabel().text)
        }

        fun hasValue(expectedValue: String) {
            assertEquals(expectedValue, driver.getThematicRevelationFieldInput().text)
        }

    }

    fun andPerspectiveCharacterField(assertions: PerspectiveCharacterAssert.() -> Unit) {
        PerspectiveCharacterAssert().assertions()
    }

    inner class PerspectiveCharacterAssert internal constructor() {

        fun hasLabel(expectedLabel: String) {
            assertEquals(expectedLabel, driver.getPerspectiveCharacterLabel().text)
        }

        fun hasValueDisplayed(expectedValue: String) {
            assertEquals(expectedValue, driver.getPerspectiveCharacterSelection().text)
        }

        fun onlyHasItems(expectedItems: List<String>) {
            assertEquals(expectedItems.toSet(), driver.getPerspectiveCharacterSelection().items.map { it.text }.toSet())
        }

        fun eachDiscouragedItemHasMessage(expectedMessageGenerator: (AvailablePerspectiveCharacterViewModel) -> String) {
            driver.getPerspectiveCharacterSelection().items.filter { it.hasClass(ComponentsStyles.discouragedSelection) }
                .forEach { menuItem ->
                    menuItem as CustomMenuItem
                    assertEquals(
                        expectedMessageGenerator(menuItem.userData as AvailablePerspectiveCharacterViewModel),
                        (menuItem.content?.properties?.get("javafx.scene.control.Tooltip") as? Tooltip)?.text
                    )
                }
        }

    }

    fun onlyHasArcSections(expectedArcSections: List<String>) {
        assertEquals(
            expectedArcSections.toSet(),
            driver.getArcSectionLabels().map { it.text }.toSet()
        )
    }

    fun hasArcSectionsInOrder(expectedArcSections: List<String>) {
        assertEquals(
            expectedArcSections,
            driver.getArcSectionLabels().map { it.text }
        )
    }

    fun andEachArcSection(assertions: ArcSectionAssert.(Int) -> Unit) {
        driver.getArcSectionNodes().indices.forEach {
            ArcSectionAssert(it).assertions(it)
        }
    }

    fun andSectionAt(index: Int, assertions: ArcSectionAssert.() -> Unit) {
        ArcSectionAssert(index).assertions()
    }

    inner class ArcSectionAssert internal constructor(private val index: Int) {

        fun hasValue(expectedValue: String) {
            assertEquals(expectedValue, driver.getArcSectionValueInputs()[index].text)
        }

        fun hasLabel(expectedLabel: String) {
            assertEquals(expectedLabel, driver.getArcSectionLabel(index).text)
        }

        fun hasId(expectedId: String) {
            assertEquals(expectedId, driver.getArcSectionNodes()[index].id)
        }

        fun hasRemoveButton() {
            assertNotNull(driver.getArcSectionRemoveButton(index))
        }

        fun doesNotHaveRemoveButton() {
            assertNull(driver.getArcSectionRemoveButton(index))
        }

        fun andRemoveButton(assertions: ButtonAssert.() -> Unit) {
            (Assertions.assertThat(driver.getArcSectionRemoveButton(index)!! as Button) as ButtonAssert).assertions()

        }

    }

    fun andAtLeastOneSectionTypeMenu(assertions: SectionTypeMenuAssert.() -> Unit) {
        val results = driver.getSectionTypeSelections().map {
            try {
                SectionTypeMenuAssert(it).assertions()
                null
            } catch(t: Throwable) {
                t
            }
        }
        assertTrue(results.any { it == null}) { "No section type menu passed the test: \n${results.filterNotNull().joinToString("\n")}" }
    }
    fun andSectionTypeMenu(assertions: SectionTypeMenuAssert.() -> Unit) {
        driver.getSectionTypeSelections().forEach {
            SectionTypeMenuAssert(it).assertions()
        }
    }

    inner class SectionTypeMenuAssert internal constructor(private val selection: MenuButton) {

        fun onlyHasItems(expectedItems: List<String>) {
            val expectedItemSet = expectedItems.toSet()
            assertEquals(expectedItemSet, selection.items.map { it.text }.toSet())
        }

        fun hasItems(expectedItems: List<String>) {
            val itemSet = selection.items.groupBy { it.text }
            expectedItems.forEach {
                assertTrue(it in itemSet) { "Section Type Menu does not contain $it.  Contained items: ${itemSet.keys}" }
            }
        }

        /**
         * first part of each pair is the expected label
         * second part of each pair is the expected usability: true meaning usable, false meaning already used
         */
        fun hasItemsInOrder(expectedItems: List<Pair<String, Boolean>>) {
            selection.items.forEachIndexed { index, menuItem ->
                val expectedLabel = expectedItems[index].first
                val expectedUsability = expectedItems[index].second
                if (! expectedUsability) {
                    menuItem as CustomMenuItem
                    assertTrue(menuItem.hasClass(ComponentsStyles.discouragedSelection)) {
                        "Un-creatable section type is not discouraged from being selected"
                    }
                    assertEquals(expectedLabel, (menuItem.content as Label).text)
                } else {
                    assertFalse(menuItem.hasClass(ComponentsStyles.discouragedSelection))
                    assertEquals(expectedLabel, menuItem.text)
                }
            }
        }

        fun eachDiscouragedItemHasMessage(expectedMessageGenerator: (MoralArgumentSectionTypeViewModel) -> String) {
            selection.items.filterNot {
                val vm = it.userData as MoralArgumentSectionTypeViewModel
                vm.canBeCreated
            }.forEach { menuItem ->
                menuItem as CustomMenuItem
                assertTrue(menuItem.hasClass(ComponentsStyles.discouragedSelection)) {
                    "Un-creatable section type is not discouraged from being selected"
                }
                assertEquals(
                    expectedMessageGenerator(menuItem.userData as MoralArgumentSectionTypeViewModel),
                    (menuItem.content?.properties?.get("javafx.scene.control.Tooltip") as? Tooltip)?.text
                )
            }
        }

    }

}