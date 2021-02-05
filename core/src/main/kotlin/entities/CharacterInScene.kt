package com.soyle.stories.entities

import com.soyle.stories.scene.CharacterArcSectionIsNotPartOfCharactersArc
import com.soyle.stories.scene.SceneAlreadyCoversCharacterArcSection
import java.util.*

class CharacterInScene(
    val characterId: Character.Id,
    val sceneId: Scene.Id,
    val characterName: String,
    val motivation: String?,
    val coveredArcSections: List<CharacterArcSection.Id>
) {

    private fun copy(
        characterName: String = this.characterName,
        motivation: String? = this.motivation,
        coveredArcSections: List<CharacterArcSection.Id> = this.coveredArcSections
    ) = CharacterInScene(characterId, sceneId, characterName, motivation, coveredArcSections)

    fun withCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
    {
        if (characterArcSection.characterId != characterId) throw CharacterArcSectionIsNotPartOfCharactersArc(
            characterId.uuid,
            characterArcSection.id.uuid,
            characterArcSection.characterId.uuid
        )
        if (characterArcSection.id in coveredArcSections) throw SceneAlreadyCoversCharacterArcSection(
            sceneId.uuid,
            characterId.uuid,
            characterArcSection.id.uuid
        )
        return copy(coveredArcSections = coveredArcSections + characterArcSection.id)
    }

    fun withoutCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
    {
        if (characterArcSection.characterId != characterId) throw CharacterArcSectionIsNotPartOfCharactersArc(
            characterId.uuid,
            characterArcSection.id.uuid,
            characterArcSection.characterId.uuid
        )
        return copy(coveredArcSections = coveredArcSections.filter { it != characterArcSection.id })
    }

}