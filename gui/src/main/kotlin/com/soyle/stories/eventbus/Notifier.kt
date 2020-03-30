package com.soyle.stories.eventbus

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
    fun addListener(listener: Listener) {
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

    protected fun notifyAll(block: (Listener) -> Unit) {
        val listeners = synchronized(this) {
            this.listeners.toList()
        }
        listeners.forEach {
            it.get()?.also(block)
        }
    }

}