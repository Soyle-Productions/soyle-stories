package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import java.util.*

sealed class SymbolicRepresentationAddedToOpposition {
    abstract val themeId: UUID
    abstract val valueWebId: UUID
    abstract val valueWebName: String
    abstract val oppositionId: UUID
    abstract val oppositionName: String

    abstract fun itemId(): UUID
    abstract val itemName: String
}

class CharacterAddedToOpposition(
    override val themeId: UUID,
    override val valueWebId: UUID,
    override val valueWebName: String,
    override val oppositionId: UUID,
    override val oppositionName: String,
    override val itemName: String,
    val characterId: UUID
) : SymbolicRepresentationAddedToOpposition() {
    override fun itemId(): UUID = characterId
}

class LocationAddedToOpposition(
    override val themeId: UUID,
    override val valueWebId: UUID,
    override val valueWebName: String,
    override val oppositionId: UUID,
    override val oppositionName: String,
    override val itemName: String,
    val locationId: UUID
) : SymbolicRepresentationAddedToOpposition() {
    override fun itemId(): UUID = locationId
}

class SymbolAddedToOpposition(
    override val themeId: UUID,
    override val valueWebId: UUID,
    override val valueWebName: String,
    override val oppositionId: UUID,
    override val oppositionName: String,
    override val itemName: String,
    val symbolId: UUID
) : SymbolicRepresentationAddedToOpposition() {
    override fun itemId(): UUID = symbolId
}