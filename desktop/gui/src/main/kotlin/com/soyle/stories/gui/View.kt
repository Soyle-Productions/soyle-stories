package com.soyle.stories.gui

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface View<VM> {
    fun updateIf(condition: VM.() -> Boolean, update: VM.() -> VM)
    fun updateOrInvalidated(update: VM.() -> VM)

    interface Nullable<VM : Any> : View<VM> {
        val viewModel: VM?
        fun update(update: VM?.() -> VM)
    }
}