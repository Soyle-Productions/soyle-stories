package com.soyle.studio.gui

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:09 PM
 */
interface View<VM> {
    fun update(update: VM.() -> VM)
}