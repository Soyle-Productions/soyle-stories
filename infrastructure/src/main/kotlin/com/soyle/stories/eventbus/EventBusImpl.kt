package com.soyle.stories.eventbus

import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:21 PM
 */
object EventBusImpl : EventBus {

    override fun publishAll(events: List<*>) {
        println(Date().toString())
        events.forEach {
            println("   $it")
        }
    }

}