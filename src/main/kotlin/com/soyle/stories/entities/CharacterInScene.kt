package com.soyle.stories.entities

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

    fun withCoveredArcSection(characterArcSectionId: CharacterArcSection.Id): CharacterInScene
    {
        if (characterArcSectionId in coveredArcSections) throw SceneAlreadyCoversCharacterArcSection(
            sceneId.uuid,
            characterId.uuid,
            characterArcSectionId.uuid
        )
        return copy(coveredArcSections = coveredArcSections + characterArcSectionId)
    }

}