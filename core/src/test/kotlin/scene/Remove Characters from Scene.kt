package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import java.util.stream.Stream

class `Remove Characters from Scene` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    private val character = given.`a character`().`has been created in the`(project)
    private val scene = given.`a scene`(named = "Big Battle").`has been created in the`(project)
    private val storyEvent = then.`a story event`(named = "Big Battle").`should have been created in`(project)

    init {
        given.the(character).`has been involved in the`(storyEvent)
    }

    @Test
    fun `Attempt to Remove Character while Still Involved in Story Events`() {
        given.the(character).`has been included in the`(scene)

        val storyEventsInScene = `when`.`the user`().`lists the story events covered by the`(scene, andInvolveThe = character)

        then.the(storyEventsInScene).`should include the`(storyEvent)
    }

    @Test
    fun `Remove Character while Still Involved in Story Events`() {
        val charactersInScene = `when`.`the user`().`lists the characters in the`(scene)

        with(then) {
            then.the(scene).`should not include the`(character)
            but.the(charactersInScene).`should include the`(character)
            and.the(storyEvent).`should involve the`(character)
        }
    }

    @Test
    fun `Attempt to Remove Character without Being Involved in Story Events`() {
        given.the(character).`has been removed from the`(storyEvent)

        val charactersInScene = `when`.`the user`().`lists the characters in the`(scene)

        with(then) {
            then.the(scene).`should not include the`(character)
            and.the(charactersInScene).`should not include the`(character)
        }
    }

    @Nested
    inner class `Rule - Implicitly Included Characters should be automatically removed from a scene when they are no longer backed`
    {

        @Test
        fun `Remove a Covered Story Event with Involved Character`() {
            given.the(storyEvent).`has been uncovered`()

            val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

            then.the(sceneCharacters).`should not include the`(character)
        }

        @Test
        fun `Remove Included Character from Story`() {
            given.the(character).`has been removed from the`(project)

            val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

            then.the(sceneCharacters).`should include the`(character)
            then.the(character).`in the`(sceneCharacters).`should be flagged as removed from the story`()
        }

        @Test
        fun `Delete Covered Story Event with Involved Character`() {
            given.the(storyEvent).`has been removed from the story`()

            val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

            then.the(sceneCharacters).`should not include the`(character)
        }

        @Test
        fun `Remove a Character from a Covered Story Event`() {
            given.the(character).`has been removed from the`(storyEvent)

            val sceneCharacters = `when`.`the user`().`lists the characters in the`(scene)

            then.the(sceneCharacters).`should not include the`(character)
        }

    }

    @Nested
    inner class `Can detect potential of removing implicitly included character`
    {

        init {
            given.the(character).`has been removed from the`(scene)
        }

        @TestFactory
        fun `Get potential changes before removing character from scene`(): List<DynamicNode> {
            return listOf(
                `when`.`the user`().`lists the potential changes of`().the(storyEvent).`being uncovered`(),
                `when`.`the user`().`lists the potential changes of`().the(storyEvent).`being removed from the`(project),
                `when`.`the user`().`lists the potential changes of`().the(character).`being removed from the`(project),
                `when`.`the user`().`lists the potential changes of`().the(character).`being removed from the`(storyEvent),
            ).map { potentialChanges ->
                dynamicTest("Get ${potentialChanges::class.simpleName}") {
                    then.the(potentialChanges).`should include`( effect().the(character).`will be removed from the`(scene) )
                }
            }
        }

        @TestFactory
        fun `Get potential changes when character is still involved`(): List<DynamicNode> {
            val newStoryEvent = given.`a story event`().`has been created in the`(project)
            given.the(character).`has been involved in the`(newStoryEvent)
            given.the(newStoryEvent).`has been covered by the`(scene)

            return listOf(
                `when`.`the user`().`lists the potential changes of`().the(storyEvent).`being uncovered`(),
                `when`.`the user`().`lists the potential changes of`().the(storyEvent).`being removed from the`(project),
                `when`.`the user`().`lists the potential changes of`().the(character).`being removed from the`(storyEvent),
            ).map { potentialChanges ->
                dynamicTest("Get ${potentialChanges::class.simpleName}") {
                    then.the(potentialChanges).`should not include`( effect().the(character).`will be removed from the`(scene) )
                }
            }

        }

    }

}