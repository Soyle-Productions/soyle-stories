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
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.text.Text
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

		val isToolFocused = object : Conditional {
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				return openTool.get(double)?.owningTab?.let {
					it.tabPane?.selectionModel?.selectedItem == it
				} ?: false
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

		private val addCharacterButton = object : ReadOnlyDependentProperty<MenuButton>
		{
			override fun get(double: SoyleStoriesTestDouble): MenuButton? {
				val tool = openTool.get(double) ?: return (null).also { UATLogger.log("Scene Details not yet open") }
				return from(tool.root).lookup(".add-character").queryAll<MenuButton>().firstOrNull()
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

		fun motivationTextFor(character: Character) = object : ReadOnlyDependentProperty<String> {
			override fun get(double: SoyleStoriesTestDouble): String? {
				val tool = openTool.get(double) ?: return null
				return (from(tool.root).lookup(".included-character").queryAll<Field>().find {
					it.text == character.name
				}?.lookup(".text-field") as? TextField)?.text ?: ""
			}
		}

		val previouslySetToolTip: PreviouslySetToolTipDriver = object : PreviouslySetToolTipDriver
		{
			override val openTooltip = object : DependentProperty<Tooltip> {
				override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()

				override fun get(double: SoyleStoriesTestDouble): Tooltip? {
					return characterFields(double).asSequence()
					  .mapNotNull { (it.lookup(".previously-set-tip") as? Hyperlink)?.tooltip }
					  .find { it.isShowing }
				}

				override fun check(double: SoyleStoriesTestDouble): Boolean = get(double)?.isShowing == true

				override fun whenSet(double: SoyleStoriesTestDouble) {
					interact {
						characterFields(double).asSequence()
						  .mapNotNull { (it.lookup(".previously-set-tip") as? Hyperlink) }
						  .filter { it.tooltip != null }
						  .firstOrNull()
						  ?.let {
							  clickOn(it, MouseButton.PRIMARY)
						  }
					}
				}
			}
			override val sceneName: ReadOnlyDependentProperty<String> = object : ReadOnlyDependentProperty<String> {
				override fun get(double: SoyleStoriesTestDouble): String? {
					val tooltip = openTooltip.get(double) ?: return (null).also { UATLogger.log("tooltip not yet open")}
					val tooltipGraphic = tooltip.graphic ?: return (null).also { UATLogger.log("no graphic on tooltip")}
					val sceneName = from(tooltipGraphic).lookup(".hyperlink").queryAll<Hyperlink>().firstOrNull()
					  ?: return (null).also { UATLogger.log("no scene name link in tooltip graphic")}
					return sceneName.text
				}
			}
			override val motivation: ReadOnlyDependentProperty<String> = object : ReadOnlyDependentProperty<String> {
				override fun get(double: SoyleStoriesTestDouble): String? {
					val tooltipGraphic = openTooltip.get(double)?.graphic ?: return null
					val motivation = from(tooltipGraphic).lookup(".motivation").queryAll<Text>().firstOrNull()
					return motivation?.text
				}
			}
			override fun whenSceneNameSelected(double: SoyleStoriesTestDouble) {
				val tooltip = openTooltip.get(double) ?: return (Unit).also { UATLogger.log("tooltip not yet open")}
				val tooltipGraphic = tooltip.graphic ?: return (Unit).also { UATLogger.log("no graphic on tooltip")}
				val sceneName = from(tooltipGraphic).lookup(".hyperlink").queryAll<Hyperlink>().firstOrNull()
				  ?: return (Unit).also { UATLogger.log("no scene name link in tooltip graphic")}
				interact {
					clickOn(sceneName, MouseButton.PRIMARY)
				}
			}
		}

		fun previouslySetToolTipFor(character: Character): PreviouslySetToolTipDriver = object : PreviouslySetToolTipDriver
		{
			override val openTooltip = object : DependentProperty<Tooltip> {
				override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf()

				override fun get(double: SoyleStoriesTestDouble): Tooltip? {
					return listedCharacter(character).previouslySetTip(double)?.tooltip
				}

				override fun check(double: SoyleStoriesTestDouble): Boolean = get(double)?.isShowing == true

				override fun whenSet(double: SoyleStoriesTestDouble) {
					interact {
						listedCharacter(character).previouslySetTip(double)?.let {
							clickOn(it, MouseButton.PRIMARY)
						}
					}
				}
			}
			override val sceneName: ReadOnlyDependentProperty<String> = previouslySetToolTip.sceneName
			override val motivation: ReadOnlyDependentProperty<String> = previouslySetToolTip.motivation
			override fun whenSceneNameSelected(double: SoyleStoriesTestDouble) = previouslySetToolTip.whenSceneNameSelected(double)
		}

		private fun characterFields(double: SoyleStoriesTestDouble): Set<Field> {
			val tool = openTool.get(double) ?: return setOf()
			return from(tool.root).lookup(".included-character").queryAll<Field>()
		}

		fun hasListedCharacter(character: Character.Id) = object : Conditional
		{
			override fun check(double: SoyleStoriesTestDouble): Boolean {
				return characterFields(double).find {
					it.id == character.uuid.toString()
				} != null
			}
		}

		fun listedCharacter(character: Character): ListedCharacterDriver = object : ListedCharacterDriver
		{
			fun field(double: SoyleStoriesTestDouble): Field?
			{
				return characterFields(double).find {
					it.id == character.id.uuid.toString()
				}
			}
			override fun previouslySetTip(double: SoyleStoriesTestDouble): Hyperlink?
			{
				return field(double)?.lookup(".previously-set-tip") as? Hyperlink
			}

			override val isListed: Conditional = object : Conditional
			{
				override fun check(double: SoyleStoriesTestDouble): Boolean {
					return field(double) != null
				}
			}

			override val motivationText: ReadOnlyDependentProperty<String> = object : ReadOnlyDependentProperty<String> {
				override fun get(double: SoyleStoriesTestDouble): String? {
					return (field(double)?.lookup(".text-field") as? TextField)?.text ?: ""
				}
			}

			override val isPreviouslySetTipVisible: Conditional = object : Conditional {
				override fun check(double: SoyleStoriesTestDouble): Boolean {
					return previouslySetTip(double)?.visibleProperty()?.get() ?: false
				}
			}
			override val isResetButtonVisible: Conditional = object : Conditional {
				override fun check(double: SoyleStoriesTestDouble): Boolean {
					return (field(double)?.lookup(".reset-button") as? Hyperlink)?.visibleProperty()?.get() ?: false
				}
			}

			override fun whenResetButtonSelected(double: SoyleStoriesTestDouble) {
				(field(double)?.lookup(".reset-button") as? Hyperlink)?.let {
					interact {
						clickOn(it, MouseButton.PRIMARY)
					}
				}
			}

			override val characterName: ReadOnlyDependentProperty<String> = object : ReadOnlyDependentProperty<String>
			{
				override fun get(double: SoyleStoriesTestDouble): String? {
					return field(double)?.text
				}
			}
		}
	}

	interface ListedCharacterDriver {
		val motivationText: ReadOnlyDependentProperty<String>
		val isListed: Conditional
		val isPreviouslySetTipVisible: Conditional
		val isResetButtonVisible: Conditional
		val characterName: ReadOnlyDependentProperty<String>
		fun previouslySetTip(double: SoyleStoriesTestDouble): Hyperlink?
		fun whenResetButtonSelected(double: SoyleStoriesTestDouble)
	}

	interface PreviouslySetToolTipDriver {
		val openTooltip: DependentProperty<Tooltip>
		val sceneName: ReadOnlyDependentProperty<String>
		val motivation: ReadOnlyDependentProperty<String>
		fun whenSceneNameSelected(double: SoyleStoriesTestDouble)
	}

	fun toolFor(scene: Scene) = ScopedDriver(scene)

}