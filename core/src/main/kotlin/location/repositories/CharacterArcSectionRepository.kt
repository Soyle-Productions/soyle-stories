package com.soyle.stories.location.repositories

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location

interface CharacterArcSectionRepository {
	fun getCharacterArcSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArcSection>
	fun updateCharacterArcSections(characterArcSections: Set<CharacterArcSection>)
}