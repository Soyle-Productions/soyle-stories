package com.soyle.stories.storyevent

import com.soyle.stories.DependentProperty
import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.StoryEventDetailsToolDriver.interact
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetails
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsScope
import com.soyle.stories.testutils.findComponentsInScope
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import org.testfx.framework.junit5.ApplicationTest

object StoryEventDetailsToolDriver : ApplicationTest() {

	fun openToolWith(storyEventId: StoryEvent.Id) = object: DependentProperty<StoryEventDetails> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  StoryEventsDriver.storyEventCreated()::given
		)
		override fun get(double: SoyleStoriesTestDouble): StoryEventDetails? {
			val projectScope = ProjectSteps.getProjectScope(double) ?: return null
			val scope = projectScope.toolScopes.find { it is StoryEventDetailsScope && it.storyEventId == storyEventId.uuid.toString() }
			  ?: return null
			val component = findComponentsInScope<StoryEventDetails>(scope).singleOrNull() ?: return null
			return component.takeIf { it.currentStage?.isShowing == true }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val projectScope = ProjectSteps.getProjectScope(double)!!
			interact {
				projectScope.get<OpenToolController>().openStoryEventDetailsTool(storyEventId.uuid.toString())
			}
		}
	}

	private fun locationDropDown(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val tool = openToolWith(storyEventId).get(double) ?: return null
			return from(tool.root).lookup("#location-select").queryAll<Button>().firstOrNull()
		}
	}

	fun disabledLocationDropDown(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			return locationDropDown(storyEventId).get(double)?.takeIf { it.isDisable }
		}
	}

	fun enabledLocationDropDown(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			return locationDropDown(storyEventId).get(double)?.takeUnless { it.isDisable }
		}
	}

	fun visibleLocationDropDownMenu(storyEventId: StoryEvent.Id) = object : DependentProperty<ContextMenu> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { it: SoyleStoriesTestDouble -> LocationSteps.givenNumberOfLocationsHaveBeenCreated(it, 1) } as (SoyleStoriesTestDouble) -> Unit,
		  openToolWith(storyEventId)::given
		)

		override fun get(double: SoyleStoriesTestDouble): ContextMenu? {
			return locationDropDown(storyEventId).get(double)?.contextMenu?.takeIf { it.isShowing }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val dropDown = locationDropDown(storyEventId).get(double)!!
			interact {
				dropDown.contextMenu.show(dropDown, Side.BOTTOM, 0.0, 0.0)
			}
		}
	}

	fun locationDropDownMenuItems(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<List<MenuItem>> {
		override fun get(double: SoyleStoriesTestDouble): List<MenuItem> {
			return visibleLocationDropDownMenu(storyEventId).get(double)?.items ?: emptyList()
		}
	}

	fun locationDropDownMenuItemCount(storyEventId: StoryEvent.Id, double: SoyleStoriesTestDouble): Int {
		return locationDropDownMenuItems(storyEventId).get(double)!!.size
	}

	fun characterDropDown(storyEventId: StoryEvent.Id) = object : DependentProperty<Button> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openToolWith(storyEventId)::given
		)
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val tool = openToolWith(storyEventId).get(double) ?: return null
			return from(tool.root).lookup(".character-select").queryAll<Button>().firstOrNull()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			get(double)!!.onAction.handle(ActionEvent())
		}
	}

	fun disabledCharacterDropDown(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			return characterDropDown(storyEventId).get(double)?.takeIf { it.isDisable }
		}
	}

	fun enabledCharacterDropDown(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			return characterDropDown(storyEventId).get(double)?.takeUnless { it.isDisable }
		}
	}

	fun visibleCharacterDropDownMenu(storyEventId: StoryEvent.Id) = object : DependentProperty<ContextMenu> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  { it: SoyleStoriesTestDouble -> CharacterDriver.givenANumberOfCharactersHaveBeenCreated(it, 1) } as (SoyleStoriesTestDouble) -> Unit,
		  openToolWith(storyEventId)::given
		)

		override fun get(double: SoyleStoriesTestDouble): ContextMenu? {
			return characterDropDown(storyEventId).get(double)?.contextMenu?.takeIf { it.isShowing }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val dropDown = characterDropDown(storyEventId).get(double)!!
			interact {
				dropDown.contextMenu.show(dropDown, Side.BOTTOM, 0.0, 0.0)
			}
		}
	}

	fun characterDropDownItems(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<List<MenuItem>> {
		override fun get(double: SoyleStoriesTestDouble): List<MenuItem> {
			return visibleCharacterDropDownMenu(storyEventId).get(double)?.items ?: emptyList()
		}
	}

	fun characterDropDownItemCount(storyEventId: StoryEvent.Id, double: SoyleStoriesTestDouble): Int {
		return characterDropDownItems(storyEventId).get(double)!!.size
	}

	fun includedCharacters(storyEventId: StoryEvent.Id) = object : ReadOnlyDependentProperty<Set<Label>> {
		override fun get(double: SoyleStoriesTestDouble): Set<Label> {
			val tool = openToolWith(storyEventId).get(double) ?: return emptySet()
			return from(tool.root).lookup(".included-character").lookup(".label").queryAll<Label>()
		}
	}

	fun removeCharacterButton(storyEventId: StoryEvent.Id, characterId: Character.Id) = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val tool = openToolWith(storyEventId).get(double) ?: return null
			val container = from(tool.root).lookup(".included-character").queryAll<HBox>().firstOrNull() ?: return null
			return from(container).lookup(".button").queryAll<Button>().firstOrNull()
		}
	}

}