package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.details.StoryEventDetailsView
import com.soyle.stories.storyevent.list.StoryEventListToolView
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.scene.control.TextField

fun StoryEventListToolView.givenStoryEventDetailsHaveBeenOpenedFor(
    storyEventId: StoryEvent.Id
): StoryEventDetailsView {
    return getStoryEventDetailsFor(storyEventId) ?: run {
        openStoryEventDetailsFor(storyEventId)
        awaitWithTimeout(1000) { getStoryEventDetailsFor(storyEventId) != null }
        getStoryEventDetailsFor(storyEventId)!!
    }
}

fun getStoryEventDetailsFor(storyEventId: StoryEvent.Id) =
    robot.getOpenDialog<StoryEventDetailsView> { it.storyEventId == storyEventId }

fun StoryEventDetailsView.involveCharacter(characterId: Character.Id) {
    val characterSelection = robot.from(this.root).lookup("#character-selection").query<TextField>()
    robot.interact {
        characterSelection.requestFocus()
    }
    val autocomplete = robot.listWindows()
        .find { it is AutoCompletePopup<*> && it.isShowing } as AutoCompletePopup<AvailableStoryElementItem<Character.Id>>
    val item = autocomplete.suggestions.find { it.entityId.id == characterId }!!
    robot.interact {
        autocomplete.fireEvent(AutoCompletePopup.SuggestionEvent(item))
    }
}

fun StoryEventDetailsView.removeCharacter(characterId: Character.Id) {
    val item = robot.from(this.root).lookup("#${characterId.uuid}").queryParent()
    val removeBtn = robot.from(item).lookup(".button").queryButton()
    robot.interact { removeBtn.fire() }
}