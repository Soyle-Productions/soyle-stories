package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.character.add.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.coverage.cover.CoverStoryEventController
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Story Event Robot` private constructor(private val projectScope: ProjectScope){

    private val givenStoryEventsByName = mutableMapOf<String, StoryEvent>()

    fun givenStoryEventExists(withName: NonBlankString, atTime: Int? = null): StoryEvent {
        val existingStoryEvent = getStoryEventByName(withName.value)
        if (existingStoryEvent == null) {
            createStoryEvent(withName, atTime)
            return getStoryEventByName(withName.value)!!.also {
                givenStoryEventsByName[it.name.value] = it
            }
        }
        return existingStoryEvent
    }

    fun getStoryEventByName(name: String): StoryEvent? {
        val storyEventRepository = projectScope.get<StoryEventRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allStoryEvents = runBlocking { storyEventRepository.listStoryEventsInProject(projectId) }
        return allStoryEvents.find { it.name.value == name } ?: givenStoryEventsByName[name]
    }

    fun getStoryEventByOldName(oldName: String): StoryEvent? = givenStoryEventsByName[oldName]

    private fun getStoryEvent(storyEventId: StoryEvent.Id): StoryEvent {
        val storyEventRepository = projectScope.get<StoryEventRepository>()
        return runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }
    }

    private fun createStoryEvent(name: NonBlankString, time: Int?) {
        val controller = projectScope.get<CreateStoryEventController>()
        controller.create()
        awaitWithTimeout(1000) {
            robot.getOpenDialog<CreateStoryEventPromptView>() != null
        }
        robot.getOpenDialog<CreateStoryEventPromptView>()!!.createStoryEventNamed(name.value, time)
    }

    fun givenStoryEventIsCoveredByScene(storyEvent: StoryEvent, sceneId: Scene.Id): StoryEvent
    {
        if (storyEvent.sceneId == sceneId) return storyEvent
        coverStoryEventInScene(storyEvent.id, sceneId)
        return getStoryEvent(storyEvent.id).also { assert(it.sceneId == sceneId) }
    }

    private fun coverStoryEventInScene(storyEventId: StoryEvent.Id, sceneId: Scene.Id) {
        val controller = projectScope.get<CoverStoryEventController>()
        runBlocking {
            controller.coverStoryEventInScene(sceneId) { storyEventId }.join()
        }
    }

    fun givenStoryEventInvolvesCharacter(storyEvent: StoryEvent, character: Character): StoryEvent {
        if (storyEvent.involvedCharacters.containsEntityWithId(character.id)) return storyEvent
        involveCharacterInStoryEvent(storyEvent.id, character.id)
        return getStoryEvent(storyEvent.id).also { assert(it.involvedCharacters.containsEntityWithId(character.id)) }
    }

    private fun involveCharacterInStoryEvent(storyEventId: StoryEvent.Id, characterId: Character.Id) {
        val controller = projectScope.get<AddCharacterToStoryEventController>()
        runBlocking {
            controller.addCharacterToStoryEvent(storyEventId) { characterId }.join()
        }
    }

    fun givenStoryEventDoesNotInvolveCharacter(storyEvent: StoryEvent, character: Character): StoryEvent {
        if (! storyEvent.involvedCharacters.containsEntityWithId(character.id)) return storyEvent
        removeCharacterFromStoryEvent(storyEvent.id, character.id)
        return getStoryEvent(storyEvent.id).also { assert(! it.involvedCharacters.containsEntityWithId(character.id)) }
    }

    private fun removeCharacterFromStoryEvent(storyEventId: StoryEvent.Id, characterId: Character.Id) {
        val controller = projectScope.get<RemoveCharacterFromStoryEventController>()
        runBlocking {
            controller.removeCharacterFromStoryEvent(
                storyEventId,
                characterId,
                { _, _ -> Confirmation(ConfirmationPrompt.Response.Confirm, true) },
                {  }
            ).join()
        }
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { `Story Event Robot`(this) } }
        }

        operator fun invoke(workbench: WorkBench): `Story Event Robot` = workbench.scope.get()
    }

}