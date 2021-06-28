package com.soyle.stories.domain.theme

import java.util.*

class SymbolicRepresentation(val entityUUID: UUID, val name: String) {
    override fun equals(other: Any?): Boolean =
        other is SymbolicRepresentation &&
                other.entityUUID == entityUUID &&
                other.name == name

    override fun hashCode(): Int {
        var result = entityUUID.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}