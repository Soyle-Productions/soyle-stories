package com.soyle.stories.scene

import com.soyle.stories.Conditional
import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.UATLogger
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.sceneDetails.SceneDetails
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.control.MenuItem
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.Field

object SceneDetailsDriver {

	class ScopedDriver internal constructor(val scene: Scene) : ApplicationTest() {

		private val scope = object : ReadOnlyDependentProperty<SceneDetailsScope> {
			override fun get(double: SoyleStoriesTestDouble): SceneDetailsScope? = ProjectSteps
			  .getProjectScope(double)
			  ?.toolScopes?.asSequence()
			  ?.filterIsInstance<SceneDetailsScope>()
			  ?.find { it.sceneId == scene.id.uuid }
		}

		val openTool = object : DependentProperty<SceneDetails> {
			override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()

			override fun get(double: SoyleStoriesTestDouble): SceneDetails? {
				val scope = scope.get(double) ?: return (null).also { UATLogger.log("No registered scene details scope") }
				return findComponentsInScope<SceneDetails>(scope).firstOrNull()
			}

			override fun whenSet(double: SoyleStoriesTestDouble) {
				interact {
					ProjectSteps.getProjectScope(double)!!.get<OpenToolController>().openSceneDetailsTool(scene.id.uuid.toString())
				}
			}
		}

		private val locationDropDown = object : ReadOnlyDependentProperty<Labeled>
		{
			override fun get(double: SoyleStoriesTestDouble): Labeled? {
				val tool = openTool.get(double) ?: return null
				return from(tool.root).lookup(".location-select").queryAll<Labeled>().firstOrNull()
			}
		}

		fun locationDropDownItems(double: SoyleStoriesTestDouble): List<MenuItem>? {
			return locationDropDown.get(double)?.contextMenu?.items
		}

		val isLocationDropDownDisabled = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean
			{
				val dropDown = locationDropDown.get(double) ?: return false
				return dropDown.disableProperty().value
			}
		}

		val locationDropDownText = object : ReadOnlyDependentProperty<String>
		{
			override fun get(double: SoyleStoriesTestDouble): String? {
				val dropDown = locationDropDown.get(double) ?: return null
				return dropDown.text
			}
		}

		val selectLocationDropDownItem = object : DependentProperty<Unit>
		{
			override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()
			override fun get(double: SoyleStoriesTestDouble): Unit? = Unit
			override fun whenSet(double: SoyleStoriesTestDouble) {
				val items = locationDropDownItems(double) ?: return
				interact { items.first().fire() }
			}
		}

		private val addCharacterButton = object : ReadOnlyDependentProperty<Button>
		{
			override fun get(double: SoyleStoriesTestDouble): Button? {
				val tool = openTool.get(double) ?: return (null).also { UATLogger.log("Scene Details not yet open") }
				return from(tool.root).lookup(".add-character").queryAll<Button>().firstOrNull()
			}
		}

		val isAddCharacterButtonDisabled = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				val dropDown = addCharacterButton.get(double) ?: return (false).also { UATLogger.log("add character button not found") }
				return dropDown.disableProperty().get()
			}
		}
		val isIncludedCharacterListEmpty = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				val tool = openTool.get(double) ?: return false
				return from(tool.root).lookup(".included-character").queryAll<Node>().isEmpty()
			}
		}
		fun includedCharacterListHasAll(characters: List<Character>) = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				val tool = openTool.get(double) ?: return false
				val characterNames = characters.map { it.name }.toSet()
				val fieldLabels = from(tool.root).lookup(".included-character").queryAll<Field>().map { it.label.text }.toSet()
				return characterNames == fieldLabels
			}
		}
		fun includedCharacterListHas(character: Character) = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				val tool = openTool.get(double) ?: return false
				return from(tool.root).lookup(".included-character").queryAll<Field>().find {
					it.label.text == character.name
				} != null
			}
		}
	}

	fun toolFor(scene: Scene) = ScopedDriver(scene)

}