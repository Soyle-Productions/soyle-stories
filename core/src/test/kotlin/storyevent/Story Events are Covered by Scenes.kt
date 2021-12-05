package com.soyle.stories.core.storyevent

import com.soyle.stories.core.IntTest
import kotlin.test.Test
import org.junit.jupiter.api.Nested

class `Story Events are Covered by Scenes` : IntTest() {

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`(named = "Big Battle").`has been created in the`(project)

    @Test
    fun `Create Story Event with Scene`() {
        with(then) {
            val storyEvent = `a story event`(named = "Big Battle").`should have been created in`(project)
            and the storyEvent `should be covered by the` scene
        }
    }

    @Test
    fun `Uncover Story Event`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in the` project

        `when`.the(storyEvent).`is uncovered`()

        then the storyEvent `should not be covered by the` scene
    }

    @Test
    fun `Delete Scene Covering Story Event`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in the` project

        `when`.the(scene).`is deleted`()

        then the storyEvent `should not be covered by the` scene
    }

    @Nested
    inner class `Rule - Story Events May Only be Covered by a Single Scene` {

        @Test
        fun `Cover Story Event with New Scene`() {
            val newScene = given.`a scene`(named = "Large Conflict") `has been created in the` project
            val storyEvent = given.`a story event`(named = "Big Battle") `has been created in the` project

            `when` the storyEvent `is covered by the` newScene

            then the storyEvent `should not be covered by the` scene
        }

    }

}