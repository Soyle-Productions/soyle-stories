package com.soyle.stories.common.builders

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget

// [op] intentionally left as [EventTarget].() -> Unit to enforce builder API on calling side.
@ViewBuilder
inline fun <E : EventTarget> E.build(op: EventTarget.() -> Unit): E {
    op()
    return this
}