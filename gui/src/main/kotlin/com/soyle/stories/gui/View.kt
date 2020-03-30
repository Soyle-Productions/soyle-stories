package com.soyle.stories.gui

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:09 PM
 */
interface View<VM> {
    fun update(update: VM.() -> VM)

    interface Nullable<VM : Any> {
        fun update(update: VM?.() -> VM)
        fun updateOrInvalidated(update: VM.() -> VM)
    }
}