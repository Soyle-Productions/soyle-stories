package com.soyle.stories.project.layout

import com.soyle.stories.layout.entities.Tool
import kotlin.reflect.KClass

fun KClass<out Tool<*>>.toPresentableToolName(): String? {
    return when (this) {
        Tool.CharacterList::class -> "Characters"
        Tool.LocationList::class -> "Locations"
        Tool.SceneList::class -> "Scenes"
        Tool.StoryEventList::class -> "Story Events"
        Tool.StoryEventDetails::class -> "Story Event Details"
        Tool.BaseStoryStructure::class -> "Base Story Structure"
        Tool.CharacterComparison::class -> "Character Comparison"
        else -> null
    }
}