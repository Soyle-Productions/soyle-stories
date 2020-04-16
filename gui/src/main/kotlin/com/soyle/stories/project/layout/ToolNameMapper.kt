package com.soyle.stories.project.layout

fun String.toPresentableToolName(): String? {
    return when (this) {
        "CharacterList" -> "Characters"
        "LocationList" -> "Locations"
        "BaseStoryStructure" -> "Base Story Structure"
        "CharacterComparison" -> "Character Comparison"
        else -> null
    }
}