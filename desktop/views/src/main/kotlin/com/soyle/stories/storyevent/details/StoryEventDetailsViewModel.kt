package com.soyle.stories.storyevent.details

import com.soyle.stories.common.doNothing
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.character.add.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.character.remove.ConfirmRemoveCharacterFromStoryEventPrompt
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventRamificationsReport
import com.soyle.stories.storyevent.character.remove.ramifications.removeCharacterFromStoryEventRamifications
import com.soyle.stories.storyevent.character.remove.removeCharacterFromStoryEventPrompt
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventCharacter
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventDetails
import com.soyle.stories.usecase.storyevent.getStoryEventDetails.StoryEventLocation
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

interface StoryEventDetailsDependencies {
    val asyncScope: CoroutineScope
    val guiContext: CoroutineContext

    val projectScope: ProjectScope

    val getStoryEventDetails: GetStoryEventDetails
    val addCharacterToStoryEventController: AddCharacterToStoryEventController
    val removeCharacterFromStoryEventController: RemoveCharacterFromStoryEventController
}

class StoryEventDetailsViewModel(
    val storyEventId: ObservableValue<StoryEvent.Id?> = objectProperty(null),

    dependencies: StoryEventDetailsDependencies? = null
) {

    private val details = objectProperty<StoryEventDetails?>(null)

    val isLoading: ObservableValue<Boolean> = details.isNull

    val name = details.stringBinding { it?.name }

    private val _location = details.objectBinding { it?.location }
    fun location(): ObservableValue<StoryEventLocation?> = _location
    val location: StoryEventLocation? by _location

    fun locationName() = _location.stringBinding { it?.name }

    private val _characters = observableListOf<StoryEventCharacterViewModel>()

    init {
        val observableMap = observableMapOf<Character.Id, StoryEventCharacterViewModel>()
        details.onChange {
            val list = it?.includedCharacters
            if (list == null) {
                observableMap.clear()
                return@onChange
            }
            list.forEach {
                observableMap.getOrPut(it.character) { StoryEventCharacterViewModel() }
                    .itemProperty.set(it)
            }
            val includedKeys = list.map { it.character }.toSet()
            observableMap.keys.removeIf { it !in includedKeys }
        }
        observableMap.addListener(MapChangeListener {
            if (it.wasRemoved()) _characters.remove(it.valueRemoved)
            if (it.wasAdded()) _characters.add(it.valueAdded)
        })
    }

    fun characters(): ObservableList<StoryEventCharacterViewModel> = _characters

    private val _availableCharacters = objectProperty<AvailableCharactersToInvolveInStoryEvent?>()
    fun availableCharacters(): ReadOnlyProperty<AvailableCharactersToInvolveInStoryEvent?> = _availableCharacters




    private val viewLogic = dependencies?.let { ViewLogic(it) }

    val loadAvailableCharacters: () -> Unit = viewLogic?.let { it::loadAvailableCharacters } ?: ::doNothing
    val cancelSelection: () -> Unit = viewLogic?.let { it::cancelSelection } ?: ::doNothing
    val selectCharacter: (AvailableStoryElementItem<Character.Id>) -> Unit =
        viewLogic?.let { it::selectCharacter } ?: ::doNothing

    fun removeCharacter(
        characterId: Character.Id,
        currentWindow: Window?
    ) {
        viewLogic?.removeCharacter(
            characterId,
            removeCharacterFromStoryEventPrompt(viewLogic.projectScope, currentWindow),
            removeCharacterFromStoryEventRamifications(characterId, viewLogic.projectScope)
        )
    }

    private inner class ViewLogic(dependencies: StoryEventDetailsDependencies) :
        StoryEventDetailsDependencies by dependencies {

        private var characterSelection: CompletableDeferred<Character.Id>? = null

        fun loadAvailableCharacters() {
            val id = storyEventId.value ?: return
            addCharacterToStoryEventController.addCharacterToStoryEvent(id) {
                characterSelection = CompletableDeferred()
                _availableCharacters.set(it)
                characterSelection?.await().also { characterSelection = null }
            }
        }

        fun cancelSelection() {
            _availableCharacters.set(null)
            characterSelection?.cancel()
        }

        fun selectCharacter(selection: AvailableStoryElementItem<Character.Id>) {
            characterSelection?.complete(selection.entityId.id)
        }

        fun removeCharacter(
            characterId: Character.Id,
            confirmationPrompt: ConfirmRemoveCharacterFromStoryEventPrompt,
            ramificationsReport: RemoveCharacterFromStoryEventRamificationsReport
        ) {
            val id = storyEventId.value ?: return
            removeCharacterFromStoryEventController.removeCharacterFromStoryEvent(
                id,
                characterId,
                confirmationPrompt,
                ramificationsReport
            )
        }

        init {
            scopedListener(storyEventId) {
                if (it == null) {
                    details.set(null)
                    return@scopedListener
                }
                asyncScope.launch {
                    getStoryEventDetails(it) {
                        withContext(guiContext) {
                            details.set(it)
                        }
                    }
                }
            }
        }

    }

}

class StoryEventCharacterViewModel : ItemViewModel<StoryEventCharacter>() {

    private val _id = bind(StoryEventCharacter::character)
    fun id(): ReadOnlyProperty<Character.Id> = _id
    val id by _id

    private val _name = bind(StoryEventCharacter::name)
    fun name(): ReadOnlyProperty<String> = _name
    val name: String by _name

}