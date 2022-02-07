package com.soyle.stories.common

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.lang.ref.WeakReference

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:04 PM
 */
abstract class Notifier<Listener : Any> {

    private val _listeners = mutableListOf<WeakReference<Listener>>()
    protected val listeners: List<Listener>
        get() = _listeners.mapNotNull { it.get() }

    private fun findWeakReference(listener: Listener): WeakReference<Listener>? = _listeners.find { it.get() == listener }

    @Synchronized
    open fun addListener(listener: Listener) {
        val ref = findWeakReference(listener)
        if (ref == null) {
            _listeners.add(WeakReference(listener))
        }
    }

    @Synchronized
    fun removeListener(listener: Listener) {
        val ref = findWeakReference(listener)
        if (ref != null) {
            _listeners.remove(ref)
        }
    }

    fun hasListener(listener: Listener): Boolean = findWeakReference(listener) != null

    protected suspend fun notifyAll(block: suspend (Listener) -> Unit) {
        val listeners = synchronized(this) {
            val listeners = this._listeners.toList()
            this._listeners.removeAll(listeners.filter { it.get() == null })
            listeners
        }
        listeners.forEach {
            val listener = it.get()
            supervisorScope {
                launch {
                    listener?.also {
                        block(it)
                    }
                }
            }
        }
    }

}

infix fun <T : Any> T.listensTo(notifier: Notifier<T>): T {
    notifier.addListener(this)
    return this
}

infix fun <T : Any> T.isListeningTo(notifier: Notifier<T>): Boolean = notifier.hasListener(this)