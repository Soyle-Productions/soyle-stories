package com.soyle.stories.desktop.view.scene.outline

import com.soyle.stories.desktop.view.scene.outline.SceneOutlineViewAccess.Companion.access
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.SceneOutlineView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.fail

class SceneOutlineAssertions private constructor(private val access: SceneOutlineViewAccess) {
    companion object {
        fun assertThat(view: SceneOutlineView, assertions: SceneOutlineAssertions.() -> Unit) {
            SceneOutlineAssertions(view.access()).assertions()
        }
    }

    fun hasStoryEvent(storyEvent: StoryEvent) {
        val item = access.getStoryEvent(storyEvent.id) ?: fail("Did not find story event card for $storyEvent")
        assertEquals(storyEvent.name.value, item.name) { "Story event in outline does not have expected name." }
    }

    fun doesNotHaveStoryEvent(storyEvent: StoryEvent) {
        assertNull(access.getStoryEvent(storyEvent.id))
    }

    fun doesNotHaveStoryEventNamed(name: String) {
        assertNull(access.getStoryEventByName(name)) { "Should not have story event named $name" }
    }

}