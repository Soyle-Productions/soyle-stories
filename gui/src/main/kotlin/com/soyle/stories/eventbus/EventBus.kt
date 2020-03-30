package com.soyle.stories.eventbus


/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:16 PM
 */
interface EventBus {

    fun publishAll(events: List<*>)

}