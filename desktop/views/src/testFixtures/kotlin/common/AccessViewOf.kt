package com.soyle.stories.desktop.view.common

import com.soyle.stories.common.ViewOf
import javafx.scene.Node
import java.util.logging.Logger
import kotlin.reflect.KClass

inline fun <reified T : Any> Node.maybeViewOf(logFailure: Boolean = false): ViewOf<T>? {
    if (this is ViewOf<*> && viewModel is T) {
        @Suppress("UNCHECKED_CAST")
        return this as ViewOf<T>
    }
    if (logFailure) {
        runCatching { notViewOf(T::class) }
            .onFailure { Logger.getGlobal().warning(it.message) }
    }
    return null
}

inline fun <reified T : Any> Node.asViewOf(): ViewOf<T> {
    return maybeViewOf() ?: notViewOf(T::class)
}

fun <T : Any> Node.notViewOf(type: KClass<T>): Nothing {
    if (this !is ViewOf<*>) error("$this is not ViewOf")
    error("${this.viewModel} is not of type $type")
}