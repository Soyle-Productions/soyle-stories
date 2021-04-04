package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity

data class CharacterInScene(
    override val id: Character.Id,
    val sceneId: Scene.Id,
    val characterName: String,
    val motivation: String?,
    val coveredArcSections: List<CharacterArcSection.Id>
) : Entity<Character.Id> {

    val characterId
        get() = id

    private fun copy(
        characterName: String = this.characterName,
        motivation: String? = this.motivation,
        coveredArcSections: List<CharacterArcSection.Id> = this.coveredArcSections
    ) = CharacterInScene(characterId, sceneId, characterName, motivation, coveredArcSections)

    internal fun withName(name: String): CharacterInScene = copy(characterName = name)

    internal fun withCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
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

    internal fun withoutCoveredArcSection(characterArcSection: CharacterArcSection): CharacterInScene
    {
        if (characterArcSection.characterId != characterId) throw CharacterArcSectionIsNotPartOfCharactersArc(
            characterId.uuid,
            characterArcSection.id.uuid,
            characterArcSection.characterId.uuid
        )
        return copy(coveredArcSections = coveredArcSections.filter { it != characterArcSection.id })
    }

    internal fun withMotivation(motivation: String?) = copy(motivation = motivation)

}