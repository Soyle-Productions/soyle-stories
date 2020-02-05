package com.soyle.studio.common

interface Entity<Id> {
    val id: Id
    infix fun isSameEntityAs(other: Entity<*>): Boolean = other::class.java == this::class.java && other.id == this.id && this.id != null
}