package com.soyle.stories.doubles

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.location.repositories.CharacterArcSectionRepository

class CharacterArcSectionRepositoryDouble(
    private val onUpdateCharacterArcSection: (CharacterArcSection) -> Unit = {}
) : CharacterArcSectionRepository {

    val characterArcSections = mutableMapOf<CharacterArcSection.Id, CharacterArcSection>()

    fun givenCharacterArcSection(characterArcSection: CharacterArcSection) {
        characterArcSections[characterArcSection.id] = characterArcSection
    }

    override fun getCharacterArcSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArcSection> {
        return characterArcSections.values.filter { it.linkedLocation == locationId }
    }

    override fun updateCharacterArcSections(characterArcSections: Set<CharacterArcSection>) {
        characterArcSections.forEach {
            onUpdateCharacterArcSection(it)
            this.characterArcSections[it.id] = it
        }
    }

}