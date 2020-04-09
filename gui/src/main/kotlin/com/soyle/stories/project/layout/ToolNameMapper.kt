package com.soyle.stories.project.layout

fun String.toPresentableToolName(): String? {
    return when (this) {
        "CharacterList" -> "Characters"
        "BaseStoryStructure" -> "Base Story Structure"
        "CharacterComparison" -> "Character Comparison"
        else -> null
    }
}