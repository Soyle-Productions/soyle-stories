package com.soyle.stories.usecase.storyevent

import org.junit.jupiter.api.Test
import com.soyle.stories.usecase.framework.IntTest
import org.junit.jupiter.api.Nested

class `Cover Story Event in Scene Int Test` : IntTest() {

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`(named = "Big Battle").`has been created in`(project)

    @Test
    fun `Create Story Event with Scene`() {
        with(then) {
            val storyEvent = `a story event`(named = "Big Battle").`should have been created in`(project)
            and the storyEvent `should be covered by the` scene
        }
    }

    @Test
    fun `Uncover Story Event`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project

        `when`.the(storyEvent).`is uncovered`()

        then the storyEvent `should not be covered by the` scene
    }

    @Test
    fun `Delete Scene Covering Story Event`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project

        `when`.the(scene).`is deleted`()

        then the storyEvent `should not be covered by the` scene
    }

    @Nested
    inner class `Rule - Story Events May Only be Covered by a Single Scene` {

        @Test
        fun `Cover Story Event with New Scene`() {
            val newScene = given.`a scene`(named = "Large Conflict") `has been created in` project
            val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project

            `when` the storyEvent `is covered by the` newScene

            then the storyEvent `should not be covered by the` scene
        }

    }

}