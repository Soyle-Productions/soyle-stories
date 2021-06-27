package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.characterarc.usecaseControllers.PromoteMinorCharacterController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.changeCharacterChange.ChangeCharacterChangeController
import com.soyle.stories.theme.changeCharacterPerspectiveProperty.ChangeCharacterPerspectivePropertyController
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyController
import com.soyle.stories.theme.changeThemeDetails.changeCentralConflict.ChangeCentralConflictController
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.removeCharacterAsOpponent.RemoveCharacterAsOpponentController
import com.soyle.stories.theme.useCharacterAsMainOpponent.UseCharacterAsMainOpponentController
import com.soyle.stories.theme.useCharacterAsOpponent.UseCharacterAsOpponentController
import com.soyle.stories.usecase.theme.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.ListAvailableCharactersToUseAsOpponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

class CharacterConflictController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val examineCentralConflict: ExamineCentralConflictOfTheme,
    private val examineCentralConflictOutputPort: ExamineCentralConflictOfTheme.OutputPort,
    private val getAvailablePerspectiveCharacters: ListAvailablePerspectiveCharacters,
    private val getAvailablePerspectiveCharactersOutputPort: ListAvailablePerspectiveCharacters.OutputPort,
    private val listAvailableCharactersToUseAsOpponents: ListAvailableCharactersToUseAsOpponents,
    private val listAvailableCharactersToUseAsOpponentsOutputPort: ListAvailableCharactersToUseAsOpponents.OutputPort,
    private val useCharacterAsOpponentController: UseCharacterAsOpponentController,
    private val promoteMinorCharacterController: PromoteMinorCharacterController,
    private val useCharacterAsMainOpponentController: UseCharacterAsMainOpponentController,
    private val changeCentralConflictController: ChangeCentralConflictController,
    private val changeSectionValueController: ChangeSectionValueController,
    private val changeCharacterChangeController: ChangeCharacterChangeController,
    private val changeCharacterPropertyController: ChangeCharacterPropertyController,
    private val changeCharacterPerspectivePropertyController: ChangeCharacterPerspectivePropertyController,
    private val removeCharacterAsOpponentController: RemoveCharacterAsOpponentController
) : CharacterConflictViewListener, CharacterIncludedInThemeReceiver {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState(characterId: String?) {
        val preparedCharacterId = characterId?.let(UUID::fromString)
        threadTransformer.async {
            try {
                examineCentralConflict.invoke(themeId, preparedCharacterId, examineCentralConflictOutputPort)
            } catch(e: CharacterIsNotMajorCharacterInTheme) {
                if (preparedCharacterId != null && e.characterId == preparedCharacterId) {
                    promoteMinorCharacterController.promoteCharacter(themeId.toString(), preparedCharacterId.toString())
                    examineCentralConflict.invoke(themeId, preparedCharacterId, examineCentralConflictOutputPort)
                } else throw e
            }
        }
    }

    override fun getAvailableCharacters() {
        threadTransformer.async {
            getAvailablePerspectiveCharacters.invoke(
                themeId, getAvailablePerspectiveCharactersOutputPort
            )
        }
    }

    override fun getAvailableOpponents(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            listAvailableCharactersToUseAsOpponents.invoke(
                themeId, preparedCharacterId, listAvailableCharactersToUseAsOpponentsOutputPort
            )
        }
    }

    override fun addOpponent(perspectiveCharacterId: String, characterId: String) {
        useCharacterAsOpponentController.useCharacterAsOpponent(themeId.toString(), perspectiveCharacterId, characterId)
    }

    override fun makeOpponentMainOpponent(perspectiveCharacterId: String, characterId: String) {
        useCharacterAsMainOpponentController.useCharacterAsMainOpponent(themeId.toString(), perspectiveCharacterId, characterId)
    }

    override fun removeOpponent(perspectiveCharacterId: String, opponentId: String) {
        removeCharacterAsOpponentController.removeCharacterAsOpponent(themeId.toString(), perspectiveCharacterId, opponentId)
    }

    override suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme) {
        if (characterIncludedInTheme.themeId != themeId) return
        if (! characterIncludedInTheme.isMajorCharacter) return
        getValidState(characterIncludedInTheme.characterId.toString())
    }

    override fun setCentralConflict(centralConflict: String) {
        changeCentralConflictController.changeCentralConflict(themeId.toString(), centralConflict)
    }

    override fun setDesire(characterId: String, desire: String) {
        changeSectionValueController.changeDesire(themeId.toString(), characterId, desire)
    }

    override fun setPsychologicalWeakness(characterId: String, weakness: String) {
        changeSectionValueController.setPsychologicalWeakness(themeId.toString(), characterId, weakness)
    }

    override fun setMoralWeakness(characterId: String, weakness: String) {
        changeSectionValueController.setMoralWeakness(themeId.toString(), characterId, weakness)
    }

    override fun setCharacterChange(characterId: String, characterChange: String) {
        changeCharacterChangeController.changeCharacterChange(themeId.toString(), characterId, characterChange)
    }

    override fun setAttackFromOpponent(perspectiveCharacterId: String, opponentId: String, attack: String) {
        changeCharacterPerspectivePropertyController.setAttackByOpponent(
            themeId.toString(), perspectiveCharacterId, opponentId, attack
        )
    }

    override fun setCharactersSimilarities(perspectiveCharacterId: String, opponentId: String, similarities: String) {
        changeCharacterPerspectivePropertyController.setSimilaritiesBetweenCharacters(
            themeId.toString(), perspectiveCharacterId, opponentId, similarities
        )
    }

    override fun setCharacterAbilities(characterId: String, ability: String) {
        changeCharacterPropertyController.setAbility(themeId.toString(), characterId, ability)
    }




}