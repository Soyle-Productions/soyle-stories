package com.soyle.stories.gui

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:09 PM
 */
interface View<VM> {
    fun updateOrInvalidated(update: VM.() -> VM)

    interface Nullable<VM : Any> : View<VM> {
        val viewModel: VM?
        fun update(update: VM?.() -> VM)
    }
}