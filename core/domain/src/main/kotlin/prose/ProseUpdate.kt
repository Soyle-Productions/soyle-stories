package com.soyle.stories.domain.prose

import com.soyle.stories.domain.prose.events.ProseEvent

class ProseUpdate<E : ProseEvent?>(val prose: Prose, val event: E) {
    operator fun component2() = event
    operator fun component1() = prose
}