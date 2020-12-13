package com.soyle.stories.prose.proseEditor

import com.soyle.stories.entities.ProseMention

data class ProseEditorViewModel(
    val versionNumber: Long,
    val content: String,
    val mentions: List<ProseMention<*>>
)