package com.soyle.stories.theme.characterConflict

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterArcSectionValueReceiver
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.domain.character.CharacterRenamed
import com.soyle.stories.domain.character.Desire
import com.soyle.stories.domain.character.MoralWeakness
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.gui.View
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcReceiver
import com.soyle.stories.theme.changeCharacterChange.ChangedCharacterChangeReceiver
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.CharacterPerspectivePropertyChangedReceiver
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.CentralConflictChangedReceiver
import com.soyle.stories.theme.removeCharacterAsOpponent.CharacterRemovedAsOpponentReceiver
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver
import com.soyle.stories.theme.useCharacterAsMainOpponent.CharacterUsedAsMainOpponentReceiver
import com.soyle.stories.theme.useCharacterAsOpponent.CharacterUsedAsOpponentReceiver
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ArcSectionType
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.usecase.theme.changeCharacterChange.ChangedCharacterChange
import com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.usecase.theme.changeThemeDetails.CentralConflictChanged
import com.soyle.stories.usecase.theme.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.usecase.theme.examineCentralConflictOfTheme.ExaminedCentralConflict
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.removeCharacterAsOpponent.CharacterRemovedAsOpponent
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.AvailableCharactersToUseAsOpponents
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsMainOpponent
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.ListAvailableCharactersToUseAsOpponents
import java.util.*

class CharacterConflictPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterConflictViewModel>
) : ExamineCentralConflictOfTheme.OutputPort, ListAvailablePerspectiveCharacters.OutputPort,
    ListAvailableCharactersToUseAsOpponents.OutputPort, CharacterUsedAsOpponentReceiver,
    CharacterUsedAsMainOpponentReceiver,
    CentralConflictChangedReceiver, ChangedCharacterArcSectionValueReceiver, ChangedCharacterChangeReceiver,
    ChangeCharacterPropertyValue.OutputPort, CharacterPerspectivePropertyChangedReceiver, CharacterRenamedReceiver,
    CharacterRemovedAsOpponentReceiver, RemovedCharacterFromThemeReceiver,
    ArcSectionAddedToCharacterArcReceiver {

    private val themeId = UUID.fromString(themeId)

    override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
        if (response.themeId != themeId) return
        view.update {
            CharacterConflictViewModel(
                centralConflictFieldLabel = "Central Conflict",
                centralConflict = response.centralConflict,
                perspectiveCharacterLabel = "Perspective Character",
                selectedPerspectiveCharacter = response.characterChange?.let {
                    CharacterItemViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        ""
                    )
                },
                availablePerspectiveCharacters = null,
                desireLabel = "Desire",
                desire = response.characterChange?.desire ?: "",
                psychologicalWeaknessLabel = "Psychological Weakness",
                psychologicalWeakness = response.characterChange?.psychologicalWeakness ?: "",
                moralWeaknessLabel = "Moral Weakness",
                moralWeakness = response.characterChange?.moralWeakness ?: "",
                characterChangeLabel = "Character Change",
                characterChange = response.characterChange?.changeAtEnd ?: "",
                opponentSectionsLabel = response.characterChange?.let { "Opponents to ${it.characterName}" }
                    ?: "Opponents",
                attackSectionLabel = response.characterChange?.let { "How They Attack ${it.characterName}" }
                    ?: "Attacks",
                similaritiesSectionLabel = response.characterChange?.let { "Similarities to ${it.characterName}" }
                    ?: "Similarities",
                powerStatusOrAbilitiesLabel = "Power / Status / Abilities",
                mainOpponent = response.characterChange?.opponents?.singleOrNull { it.isMainOpponent }?.let {
                    CharacterChangeOpponentViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.attack,
                        it.similarities,
                        it.powerStatusOrAbility
                    )
                },
                opponents = response.characterChange?.opponents?.filterNot { it.isMainOpponent }?.map {
                    CharacterChangeOpponentViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.attack,
                        it.similarities,
                        it.powerStatusOrAbility
                    )
                } ?: emptyList(),
                availableOpponents = null
            )
        }
    }

    override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availablePerspectiveCharacters = response.map {
                    AvailablePerspectiveCharacterViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.isMajorCharacter
                    )
                }
            )
        }
    }

    override suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availableOpponents = response.map {
                    AvailableOpponentViewModel(it.characterId.toString(), it.characterName, it.includedInTheme)
                }
            )
        }
    }

    override suspend fun receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent: CharacterUsedAsMainOpponent) {
        if (characterUsedAsMainOpponent.themeId != themeId) return
        val opponentId = characterUsedAsMainOpponent.characterId.toString()
        view.updateOrInvalidated {
            if (characterUsedAsMainOpponent.opponentOfCharacterId.toString() != selectedPerspectiveCharacter?.characterId) return@updateOrInvalidated this

            val existingOpponent = opponents.find { it.characterId == opponentId }

            copy(
                mainOpponent = existingOpponent ?: CharacterChangeOpponentViewModel(
                    opponentId,
                    characterUsedAsMainOpponent.characterName,
                    "",
                    "",
                    ""
                ),
                opponents = opponents.filterNot { it.characterId == opponentId }
            )
        }
    }

    override suspend fun receiveCharacterUsedAsOpponent(characterUsedAsOpponent: CharacterUsedAsOpponent) {
        if (characterUsedAsOpponent.themeId != themeId) return
        val opponentId = characterUsedAsOpponent.characterId.toString()
        view.updateOrInvalidated {
            if (characterUsedAsOpponent.opponentOfCharacterId.toString() != selectedPerspectiveCharacter?.characterId) return@updateOrInvalidated this

            val existingOpponent = mainOpponent?.takeIf { it.characterId == opponentId }

            copy(
                mainOpponent = if (mainOpponent?.characterId == opponentId) null else mainOpponent,
                opponents = opponents + (existingOpponent
                    ?: CharacterChangeOpponentViewModel(
                        opponentId,
                        characterUsedAsOpponent.characterName,
                        "",
                        "",
                        ""
                    ))
            )
        }
    }

    override suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged) {
        if (centralConflictChanged.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                centralConflict = centralConflictChanged.centralConflict
            )
        }
    }

    override suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange) {
        if (changedCharacterChange.themeId != themeId) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != changedCharacterChange.characterId.toString())
                return@updateOrInvalidated this

            copy(
                characterChange = changedCharacterChange.characterChange
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        if (response.themeId != themeId) return
        if (response.property != ChangeCharacterPropertyValue.Property.Ability) return
        val characterId = response.characterId.toString()
        view.updateOrInvalidated {
            if (opponents.none { it.characterId == characterId } && mainOpponent?.characterId != characterId) {
                return@updateOrInvalidated this
            }

            copy(
                mainOpponent = if (mainOpponent?.characterId == characterId) {
                    mainOpponent.copy(powerStatusOrAbilities = response.newValue)
                } else mainOpponent,
                opponents = opponents.map {
                    if (it.characterId == characterId) it.copy(powerStatusOrAbilities = response.newValue)
                    else it
                }
            )
        }
    }

    override suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue) {
        if (changedCharacterArcSectionValue.themeId != themeId) return
        if (changedCharacterArcSectionValue.type !in setOf(
                ArcSectionType.Desire,
                ArcSectionType.PsychologicalWeakness,
                ArcSectionType.MoralWeakness
            )
        ) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != changedCharacterArcSectionValue.characterId.toString())
                return@updateOrInvalidated this

            when (changedCharacterArcSectionValue.type) {
                ArcSectionType.Desire -> copy(desire = changedCharacterArcSectionValue.newValue)
                ArcSectionType.PsychologicalWeakness -> copy(psychologicalWeakness = changedCharacterArcSectionValue.newValue)
                ArcSectionType.MoralWeakness -> copy(moralWeakness = changedCharacterArcSectionValue.newValue)
                else -> this // due to the type !in setOf call above, will never actually reach this line.
            }
        }
    }

    override suspend fun receiveCharacterPerspectivePropertyChanged(propertyChanged: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        if (propertyChanged.themeId != themeId) return
        if (propertyChanged.property !in setOf(
                ChangeCharacterPerspectivePropertyValue.Property.Similarities,
                ChangeCharacterPerspectivePropertyValue.Property.Attack
            )
        ) return
        val opponentId = propertyChanged.targetCharacterId.toString()
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != propertyChanged.perspectiveCharacterId.toString())
                return@updateOrInvalidated this

            fun CharacterChangeOpponentViewModel.copyWithProperty() = copy(
                attack = if (propertyChanged.property == ChangeCharacterPerspectivePropertyValue.Property.Attack) propertyChanged.newValue else attack,
                similarities = if (propertyChanged.property == ChangeCharacterPerspectivePropertyValue.Property.Similarities) propertyChanged.newValue else similarities
            )

            copy(
                mainOpponent = if (mainOpponent?.characterId == opponentId) {
                    mainOpponent.copyWithProperty()
                } else mainOpponent,
                opponents = opponents.map {
                    if (it.characterId == opponentId) it.copyWithProperty()
                    else it
                }
            )
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        val renamedCharacterId = characterRenamed.characterId.toString()
        view.updateOrInvalidated {
            copy(
                selectedPerspectiveCharacter = if (selectedPerspectiveCharacter?.characterId == renamedCharacterId) {
                    selectedPerspectiveCharacter.copy(characterName = characterRenamed.newName)
                } else selectedPerspectiveCharacter,
                availablePerspectiveCharacters = availablePerspectiveCharacters?.map {
                    if (it.characterId == renamedCharacterId) it.copy(characterName = characterRenamed.newName)
                    else it
                },
                availableOpponents = availableOpponents?.map {
                    if (it.characterId == renamedCharacterId) it.copy(characterName = characterRenamed.newName)
                    else it
                },
                mainOpponent = if (mainOpponent?.characterId == renamedCharacterId) {
                    mainOpponent.copy(characterName = characterRenamed.newName)
                } else mainOpponent,
                opponents = opponents.map {
                    if (it.characterId == renamedCharacterId) it.copy(characterName = characterRenamed.newName)
                    else it
                }
            )
        }
    }

    override suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc) {
        if (event.themeId != themeId) return
        if (event.templateSectionId !in setOf(
                Desire.id.uuid,
                PsychologicalWeakness.id.uuid,
                MoralWeakness.id.uuid
            )
        ) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != event.characterId.toString())
                return@updateOrInvalidated this

            when (event.templateSectionId) {
                Desire.id.uuid -> copy(desire = event.value)
                PsychologicalWeakness.id.uuid -> copy(psychologicalWeakness = event.value)
                MoralWeakness.id.uuid -> copy(moralWeakness = event.value)
                else -> this // due to the type !in setOf call above, will never actually reach this line.
            }
        }
    }

    override suspend fun receiveCharacterRemovedAsOpponent(characterRemovedAsOpponent: CharacterRemovedAsOpponent) {
        if (characterRemovedAsOpponent.themeId != themeId) return
        val removedCharacterId = characterRemovedAsOpponent.characterId.toString()
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != characterRemovedAsOpponent.opponentOfCharacterId.toString())
                return@updateOrInvalidated this

            copy(
                mainOpponent = mainOpponent?.takeUnless { it.characterId == removedCharacterId },
                opponents = opponents.filterNot {
                    it.characterId == removedCharacterId
                }
            )
        }
    }

    override suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme) {
        if (removedCharacterFromTheme.themeId != themeId) return
        val removedCharacterId = removedCharacterFromTheme.characterId.toString()
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId == removedCharacterId)
                copy(selectedPerspectiveCharacter = null, mainOpponent = null, opponents = emptyList())
            else
                copy(
                    mainOpponent = mainOpponent?.takeUnless { it.characterId == removedCharacterId },
                    opponents = opponents.filterNot {
                        it.characterId == removedCharacterId
                    }
                )
        }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: Exception) {
        /* no-op */
    }

}