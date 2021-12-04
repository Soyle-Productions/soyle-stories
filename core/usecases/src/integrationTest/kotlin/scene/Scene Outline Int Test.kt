package com.soyle.stories.usecase.scene

import com.soyle.stories.usecase.framework.`Int Test`
import org.junit.jupiter.api.Test

class `Scene Outline Int Test` : `Int Test`(){

    private val project = given.`a project`().`has been started`()
    private val scene = given.`a scene`(named = "Big Battle").`has been created in`(project)

    @Test
    fun `Create Scene with Initial Story Event`() {
        with(then) {
            val storyEvent = `a story event`(named = "Big Battle").`should have been created in`(project)
            and the scene `should contain the` storyEvent
        }
    }

    @Test
    fun `Add Another Story Event to Scene`() {
        val `original story event` = given.`a story event`(named = "Big Battle") `has been created in` project
        val `new story event` = given.`a story event`(named = "Something happens") `has been created in` project

        `when` the `new story event` `is covered by the` scene

        with(then) {
            then the scene `should contain the` `new story event`
            but the scene `should not contain the` `original story event`
        }
    }

    @Test
    fun `Transfer Covered Story Event to New Scene`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project
        val newScene = given.`a scene`(named = "Large Conflict") `has been created in` project

        `when` the storyEvent `is covered by the` newScene

        with(then) {
            then the newScene `should contain the` storyEvent
            but the scene `should not contain the` storyEvent
        }
    }

    @Test
    fun `Remove Story Event from Scene`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project

        `when`.the(storyEvent).`is uncovered`()

        then the scene `should not contain the` storyEvent
    }

    @Test
    fun `Delete a Covered Story Event`() {
        val storyEvent = given.`a story event`(named = "Big Battle") `has been created in` project

        `when`.the(storyEvent).`is deleted`()

        then the scene `should not contain the` storyEvent
    }

}