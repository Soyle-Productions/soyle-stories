package com.soyle.stories.domain.character.name

class CharacterNames(
    val displayName: CharacterName,
    secondary: Set<CharacterName>
) : Set<CharacterName> by (secondary + displayName) {

    val secondaryNames: Set<CharacterName> = this - displayName

    internal fun withName(name: CharacterName) = CharacterNames(displayName, secondaryNames + name)

    internal fun withoutName(name: CharacterName): CharacterNames {
        if (name !in this) return this
        return CharacterNames(displayName, secondaryNames - name)
    }

    internal fun rename(name: CharacterName, newName: CharacterName): CharacterNames {
        return when (name) {
            displayName -> CharacterNames(newName, secondaryNames)
            !in secondaryNames -> this
            else -> CharacterNames(displayName, secondaryNames - name + newName)
        }
    }

}