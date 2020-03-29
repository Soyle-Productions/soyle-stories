package com.soyle.stories.common

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:30 PM
 */
interface Entity<Id> {
    val id: Id
    infix fun isSameEntityAs(other: Entity<*>): Boolean =
        other::class.java == this::class.java && other.id == this.id && this.id != null
}