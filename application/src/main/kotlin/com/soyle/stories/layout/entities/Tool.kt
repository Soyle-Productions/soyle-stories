package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.layout.tools.ToolType
import java.util.*

class Tool(
    override val id: Id,
    val type: ToolType,
    val isOpen: Boolean
) : Entity<Tool.Id> {

    constructor(type: ToolType, isOpen: Boolean = true) : this(Id(), type, isOpen)

    fun isTemporary() = type.isTemporary

    fun closed() = Tool(id, type, false)
    fun opened() = Tool(id, type, true)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tool

        if (id != other.id) return false
        if (type != other.type) return false
        if (isOpen != other.isOpen) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + isOpen.hashCode()
        return result
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Tool($uuid)"
    }
}