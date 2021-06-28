package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme

data class IncludedCharacterViewModel(
    val id: Character.Id,
    val name: String,
    val imageResource: String,
    val roleInScene: CharacterRoleInScene?,

    val desire: String,
    val motivation: String?,
    val motivationCanBeReset: Boolean,
    val previousMotivation: PreviousMotivation?,
    val coveredArcSections: List<CoveredArcSectionViewModel>,
    val availableCharacterArcSections: List<AvailableCharacterArcViewModel>?
) {

    private val cachedHashCode: Int by lazy {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageResource.hashCode()
        result = 31 * result + (roleInScene?.hashCode() ?: 0)
        result = 31 * result + desire.hashCode()
        result = 31 * result + motivation.hashCode()
        result = 31 * result + motivationCanBeReset.hashCode()
        result = 31 * result + (previousMotivation?.hashCode() ?: 0)
        result = 31 * result + coveredArcSections.hashCode()
        result = 31 * result + (availableCharacterArcSections?.hashCode() ?: 0)
        result
    }

    override fun hashCode(): Int = cachedHashCode
}

enum class CharacterRoleInScene {
    IncitingCharacter,
    OpponentToIncitingCharacter
}

data class PreviousMotivation(
    val value: String,
    val sourceSceneId: Scene.Id,
    val sourceSceneName: String
)

data class CoveredArcSectionViewModel(
    val arcSectionId: CharacterArcSection.Id,
    val characterArcId: CharacterArc.Id,
    val displayLabel: String
)

data class AvailableCharacterArcViewModel(
    val characterArcId: CharacterArc.Id,
    val themeId: Theme.Id,
    val characterArcName: String,
    val numberOfCoveredSections: Int,
    val allSectionsCovered: Boolean,
    val sections: List<AvailableArcSectionViewModel>
)

data class AvailableArcSectionViewModel(
    val arcSectionId: CharacterArcSection.Id,
    val arcSectionLabel: String,
    val isCovered: Boolean,
    val labelWhenSelected: String
)