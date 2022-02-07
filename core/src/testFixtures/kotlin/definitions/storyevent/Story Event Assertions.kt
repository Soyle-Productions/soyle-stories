package com.soyle.stories.core.definitions.storyevent

import com.soyle.stories.core.definitions.storyevent.character.`Characters in Story Event Assertions`
import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.fail
import org.amshove.kluent.should

class `Story Event Assertions`(
    private val storyEventRepository: StoryEventRepository
) : `Story Event Steps`.Then {

    override fun `a story event`(named: String): `Story Event Steps`.Then.ExistenceAssertions = object :
        `Story Event Steps`.Then.ExistenceAssertions {
        override fun `should have been created in`(project: Project.Id): StoryEvent.Id {
            return runBlocking { storyEventRepository.listStoryEventsInProject(project) }
                .first { it.name.value == named }
                .id
        }

        override fun `should not exist`(inProject: Project.Id) {
            runBlocking { storyEventRepository.listStoryEventsInProject(inProject) }
                .find { it.name.value == named }
                ?.let { fail("Story event with name $named should not exist in $inProject, but found $it") }
        }
    }

    override fun the(storyEventId: StoryEvent.Id): `Story Event Steps`.Then.StateAssertions =
    object :
        `Story Event Steps`.Then.StateAssertions {
        private fun getStoryEvent(): StoryEvent =
            runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }

        override fun `should be at time`(time: Long) {
            val storyEvent = getStoryEvent()
            storyEvent.should(
                """
                    $storyEventId should be at time $time
                       time:   ${storyEvent.time}
                """.trimIndent()
            ) {
                this.time == time.toULong()
            }
        }

        override fun `should be covered by the`(scene: Scene.Id) {
            val storyEvent = getStoryEvent()
            storyEvent.should(
                """
                    $storyEventId should be covered by the $scene
                       Scene Id:   ${storyEvent.sceneId}
                """.trimIndent()
            ) {
                sceneId == scene
            }
        }

        override fun `should not be covered by the`(scene: Scene.Id) {
            val storyEvent = getStoryEvent()
            storyEvent.should(
                """
                    $storyEventId should not be covered by the $scene
                       Scene Id:   ${storyEvent.sceneId}
                """.trimIndent()
            ) {
                sceneId != scene
            }
        }

        override fun `should not involve any characters`() {
            val storyEvent = getStoryEvent()
            storyEvent.should(
                """
                    $storyEventId should not involve any characters
                       Involved Characters:   ${storyEvent.involvedCharacters}
                """.trimIndent()
            ) {
                involvedCharacters.isEmpty()
            }
        }

        override fun `should involve the`(character: Character.Id) {
            val storyEvent = getStoryEvent()
            storyEvent.should(
                """
                    $storyEventId should involve the $character
                       Involved Characters:   ${storyEvent.involvedCharacters}
                """.trimIndent()
            ) {
                involvedCharacters.containsEntityWithId(character)
            }
        }
    }

    override fun the(availableCharacters: AvailableCharactersToInvolveInStoryEvent): `Story Event Steps`.Then.AvailableCharactersStateAssertions =
        `Characters in Story Event Assertions`(availableCharacters)

}