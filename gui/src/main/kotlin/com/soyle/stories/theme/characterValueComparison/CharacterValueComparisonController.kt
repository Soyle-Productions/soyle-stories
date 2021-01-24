package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.changeCharacterPropertyValue.ChangeCharacterPropertyController
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonController
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonController
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemController
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import java.util.*

class CharacterValueComparisonController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val compareCharacterValues: CompareCharacterValues,
    private val compareCharacterValuesOutputPort: CompareCharacterValues.OutputPort,
    private val listCharactersAvailableToIncludeInTheme: ListCharactersAvailableToIncludeInTheme,
    private val listCharactersAvailableToIncludeInThemeOutputPort: ListCharactersAvailableToIncludeInTheme.OutputPort,
    private val listAvailableOppositionValuesForCharacterInTheme: ListAvailableOppositionValuesForCharacterInTheme,
    private val listAvailableOppositionValuesForCharacterInThemeOutputPort: ListAvailableOppositionValuesForCharacterInTheme.OutputPort,
    private val openToolController: OpenToolController,
    private val includeCharacterInComparisonController: IncludeCharacterInComparisonController,
    private val removeCharacterFromComparison: RemoveCharacterFromComparisonController,
    private val changeCharacterPropertyController: ChangeCharacterPropertyController,
    private val addSymbolicItemToOppositionController: AddSymbolicItemToOppositionController,
    private val removeSymbolicItemController: RemoveSymbolicItemController
) : CharacterValueComparisonViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            compareCharacterValues.invoke(themeId, compareCharacterValuesOutputPort)
        }
    }

    override fun openValueWebTool(themeId: String) {
        openToolController.openValueOppositionWeb(themeId)
    }

    override fun getAvailableCharacters() {
        threadTransformer.async {
            listCharactersAvailableToIncludeInTheme.invoke(
                themeId,
                listCharactersAvailableToIncludeInThemeOutputPort
            )
        }
    }

    override fun getAvailableOppositionValues(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            listAvailableOppositionValuesForCharacterInTheme.invoke(
                themeId,
                preparedCharacterId,
                listAvailableOppositionValuesForCharacterInThemeOutputPort
            )
        }
    }

    override fun addCharacter(characterId: String) {
        includeCharacterInComparisonController.includeCharacterInTheme(themeId.toString(), characterId)
    }

    override fun removeCharacter(characterId: String) {
        removeCharacterFromComparison.removeCharacter(themeId.toString(), characterId)
    }

    override fun setCharacterArchetype(characterId: String, archetype: String) {
        changeCharacterPropertyController.setArchetype(themeId.toString(), characterId, archetype)
    }

    override fun selectOppositionValueForCharacter(characterId: String, oppositionValueId: String) {
        addSymbolicItemToOppositionController.addCharacterToOpposition(oppositionValueId, characterId)
    }

    override fun removeOppositionValueFromCharacter(characterId: String, oppositionValueId: String) {
        removeSymbolicItemController.removeItemFromOpposition(oppositionValueId, characterId) { throw it }
    }

}