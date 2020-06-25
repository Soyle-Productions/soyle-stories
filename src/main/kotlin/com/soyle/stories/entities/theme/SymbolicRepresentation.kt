package com.soyle.stories.entities.theme

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import java.util.*

sealed class SymbolicRepresentation {
    abstract fun entityUUID(): UUID
    abstract val name: String
}
class SymbolicCharacter(val characterId: Character.Id, override val name: String) : SymbolicRepresentation() {
    override fun entityUUID(): UUID = characterId.uuid
}
class SymbolicLocation(val locationId: Location.Id, override val name: String) : SymbolicRepresentation() {
    override fun entityUUID(): UUID = locationId.uuid
}
class SymbolicSymbol(val symbolId: Symbol.Id, override val name: String) : SymbolicRepresentation() {
    override fun entityUUID(): UUID = symbolId.uuid
}