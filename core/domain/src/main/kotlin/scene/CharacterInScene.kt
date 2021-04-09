package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity

class CharacterInScene(
    override val id: Character.Id,
    val sceneId: Scene.Id,
    val characterName: String,
    val roleInScene: RoleInScene?,
    val desire: String,
    val motivation: String?,
    val coveredArcSections: List<CharacterArcSection.Id>
) : Entity<Character.Id> {

    constructor(sceneId: Scene.Id, id: Character.Id, name: String) : this(
        id, sceneId, name, null, "", null, emptyList()
    )

    val characterId
        get() = id

    private fun copy(
        characterName: String = this.characterName,
        roleInScene: RoleInScene? = this.roleInScene,
        desire: String = this.desire,
        motivation: String? = this.motivation,
        coveredArcSections: List<CharacterArcSection.Id> = this.coveredArcSections
    ) = CharacterInScene(characterId, sceneId, characterName, roleInScene, desire, motivation, coveredArcSections)



    internal fun withName(name: String): CharacterInScene = copy(characterName = name)

    internal fun withRoleInScene(roleInScene: RoleInScene?) = copy(roleInScene = roleInScene)

    internal fun withDesire(desire: String) = copy(desire = desire)

    internal fun withMotivation(motivation: String?) = copy(motivation = motivation)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterInScene

        if (id != other.id) return false
        if (sceneId != other.sceneId) return false
        if (characterName != other.characterName) return false
        if (roleInScene != other.roleInScene) return false
        if (motivation != other.motivation) return false
        if (coveredArcSections != other.coveredArcSections) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sceneId.hashCode()
        result = 31 * result + characterName.hashCode()
        result = 31 * result + (roleInScene?.hashCode() ?: 0)
        result = 31 * result + (motivation?.hashCode() ?: 0)
        result = 31 * result + coveredArcSections.hashCode()
        return result
    }

    override fun toString(): String {
        return "CharacterInScene(id=$id, sceneId=$sceneId, characterName='$characterName', roleInScene=$roleInScene, motivation=$motivation, coveredArcSections=$coveredArcSections)"
    }

}