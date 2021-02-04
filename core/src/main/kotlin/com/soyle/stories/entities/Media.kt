package com.soyle.stories.entities

import java.util.*

abstract class Media(val id: Id) {

    class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            return (other as? Id)?.uuid == uuid
        }
        override fun hashCode(): Int = uuid.hashCode()
        override fun toString(): String = "Media($uuid)"
    }

}