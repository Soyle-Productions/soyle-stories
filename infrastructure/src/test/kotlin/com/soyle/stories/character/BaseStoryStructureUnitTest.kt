package com.soyle.stories.character

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructure
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewListener
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.common.PairOf
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.event.ActionEvent
import javafx.scene.control.Button
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.removeFromParent
import tornadofx.text
import java.util.*

class BaseStoryStructureUnitTest : ApplicationTest() {

	val projectId = UUID.randomUUID()
	val toolId = UUID.randomUUID()
	val characterId = UUID.randomUUID()
	val themeId = UUID.randomUUID()
	val sectionId = UUID.randomUUID()
	val locationId = UUID.randomUUID()

	val projectViewModel = ProjectFileViewModel(projectId, "Untitled", "")
	val type = com.soyle.stories.layout.tools.dynamic.BaseStoryStructure(characterId, themeId)

	@BeforeEach
	fun setupFX() {
		FxToolkit.registerPrimaryStage()
	}

	@AfterEach
	fun cleanup() {
		FxToolkit.cleanupStages()
	}

	private var scope: BaseStoryStructureScope? = null
	private var getBaseStoryStructureCallCount: Int = 0
	private var changeSectionValueCall: Pair<String, String>? = null
	private var linkLocationCall: PairOf<String>? = null

	@Test
	fun getValidStateWhenCreated() {
		whenBaseStoryStructureCreated()
		assertTrue(getBaseStoryStructureCallCount == 1)
	}

	@Test
	fun `change section value on focus lost`() {
		givenBaseStoryStructureHasBeenCreated()
		givenSection(StoryStructureSectionViewModel("", sectionId.toString(), "Initial value", emptyList(), null))
		givenFirstTextInputHasBeenFocused()
		whenFirstTextInputHasValue("New value")
		whenFirstTextInputLosesFocus()
		assertEquals(sectionId.toString() to "New value", changeSectionValueCall)
	}

	@Test
	fun `link location when selected`() {
		givenBaseStoryStructureHasBeenCreated()
		givenSection(StoryStructureSectionViewModel("", sectionId.toString(), "Initial value", emptyList(), null))
		givenLocation(LocationItemViewModel(locationId.toString(), ""))
		interact {
			scope!!.get<BaseStoryStructure>().openWindow(owner = null)
			val locationSelection = openFirstLocationSelection()
			locationSelection.onAction.handle(ActionEvent())
			locationSelection.contextMenu.items.first().onAction.handle(ActionEvent())
		}
		assertEquals(sectionId.toString() to locationId.toString(), linkLocationCall)
	}

	private fun givenBaseStoryStructureHasBeenCreated() {
		if (scope == null) whenBaseStoryStructureCreated()
	}
	private fun whenBaseStoryStructureCreated() {
		scope = BaseStoryStructureScope(ProjectScope(ApplicationScope(), projectViewModel), toolId.toString(), type)
		scoped<BaseStoryStructureScope> {
			provide<BaseStoryStructureViewListener> {
				object : BaseStoryStructureViewListener {
					override fun changeSectionValue(sectionId: String, value: String) {
						changeSectionValueCall = sectionId to value
					}

					override fun getBaseStoryStructure() {
						getBaseStoryStructureCallCount++
					}

					override fun linkLocation(sectionId: String, locationId: String) {
						linkLocationCall = sectionId to locationId
					}

					override fun unlinkLocation(sectionId: String) {

					}
				}
			}
		}
		scope!!.get<BaseStoryStructure>()
	}

	private fun givenSection(section: StoryStructureSectionViewModel)
	{
		givenBaseStoryStructureHasBeenCreated()
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		interact {
			baseStoryStructure.model.sections.add(section)
		}
	}

	private fun givenLocation(location: LocationItemViewModel)
	{
		givenBaseStoryStructureHasBeenCreated()
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		interact {
			baseStoryStructure.model.availableLocations.add(location)
		}
	}

	private fun givenFirstTextInputHasBeenFocused()
	{
		givenBaseStoryStructureHasBeenCreated()
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		interact {
			baseStoryStructure.openWindow(owner = null)
			val firstTextField = from(baseStoryStructure.root).lookup(".text-field").queryTextInputControl()
			firstTextField.requestFocus()
		}
	}

	private fun whenFirstTextInputHasValue(value: String)
	{
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		interact {
			val firstTextField = from(baseStoryStructure.root).lookup(".text-field").queryTextInputControl()
			firstTextField.text = value
		}
	}
	private fun whenFirstTextInputLosesFocus()
	{
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		interact {
			baseStoryStructure.root.text { requestFocus(); removeFromParent() }
		}
	}

	private fun openFirstLocationSelection(): Button
	{
		val baseStoryStructure = scope!!.get<BaseStoryStructure>()
		var locationSelect: Button? = null
		interact {
			locationSelect = from(baseStoryStructure.root).lookup(".location-select").query()
		}
		return locationSelect as Button
	}

}