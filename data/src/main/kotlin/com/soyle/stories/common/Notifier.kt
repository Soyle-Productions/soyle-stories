package com.soyle.stories.common

import java.lang.ref.WeakReference

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:04 PM
 */
abstract class Notifier<Listener : Any> {

    private val listeners = mutableListOf<WeakReference<Listener>>()

    private fun findWeakReference(listener: Listener): WeakReference<Listener>? = listeners.find { it.get() == listener }

    @Synchronized
    open fun addListener(listener: Listener) {
        val ref = findWeakReference(listener)
        if (ref == null) {
            listeners.add(WeakReference(listener))
        }
    }

    @Synchronized
    fun removeListener(listener: Listener) {
        val ref = findWeakReference(listener)
        if (ref != null) {
            listeners.remove(ref)
        }
    }

    fun hasListener(listener: Listener): Boolean = findWeakReference(listener) != null

    protected fun notifyAll(block: (Listener) -> Unit) {
        val listeners = synchronized(this) {
            val listeners = this.listeners.toList()
            this.listeners.removeAll(listeners.filter { it.get() == null })
            listeners
        }
        listeners.forEach {
            it.get()?.also(block)
        }
    }

}

infix fun <T : Any> T.listensTo(notifier: Notifier<T>): T {
    notifier.addListener(this)
    return this
}

infix fun <T : Any> T.isListeningTo(notifier: Notifier<T>): Boolean = notifier.hasListener(this)