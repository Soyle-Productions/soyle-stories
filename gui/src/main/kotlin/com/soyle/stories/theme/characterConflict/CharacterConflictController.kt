package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.characterarc.usecaseControllers.PromoteMinorCharacterController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.changeCharacterChange.ChangeCharacterChangeController
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyController
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.updateThemeMetaData.ChangeCentralConflictController
import com.soyle.stories.theme.useCharacterAsMainOpponent.UseCharacterAsMainOpponentController
import com.soyle.stories.theme.useCharacterAsOpponent.UseCharacterAsOpponentController
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.ListAvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
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
    private val changeCharacterPropertyController: ChangeCharacterPropertyController
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

    override fun setCharacterChange(characterId: String, characterChange: String) {
        changeCharacterChangeController.changeCharacterChange(themeId.toString(), characterId, characterChange)
    }

    override fun setCharacterAbilities(characterId: String, ability: String) {
        changeCharacterPropertyController.setAbility(themeId.toString(), characterId, ability)
    }

}