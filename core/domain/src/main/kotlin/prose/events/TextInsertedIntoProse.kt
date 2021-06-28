package com.soyle.stories.domain.prose.events

import com.soyle.stories.domain.prose.Prose

class TextInsertedIntoProse(
    prose: Prose,
    val insertedText: String,
    val index: Int
) : ProseEvent(prose)