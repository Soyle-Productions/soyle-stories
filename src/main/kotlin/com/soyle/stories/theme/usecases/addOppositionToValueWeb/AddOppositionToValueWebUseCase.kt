package com.soyle.stories.theme.usecases.addOppositionToValueWeb

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.common.PairOf
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterAddedToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.validateOppositionValueName
import java.util.*

class AddOppositionToValueWebUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : AddOppositionToValueWeb {


    @Suppress("OverridingDeprecatedMember")
    override suspend fun invoke(valueWebId: UUID, output: AddOppositionToValueWeb.OutputPort) {
        invoke(AddOppositionToValueWeb.RequestModel(valueWebId), output)
    }

    override suspend fun invoke(
        request: AddOppositionToValueWeb.RequestModel,
        output: AddOppositionToValueWeb.OutputPort
    ) {
        val (theme, valueWeb) = getThemeAndValueWeb(request.valueWebId)
        val firstRepresentation = createSymbolicItemIfRequested(request)
        val (themeWithCharacter, characterInTheme) = includeCharacterInThemeIfNeeded(theme, firstRepresentation)
        val oppositionValue = createOpposition(themeWithCharacter, valueWeb, request.name, firstRepresentation)

        val response = AddOppositionToValueWeb.ResponseModel(
            oppositionAddedToValueWeb(oppositionValue, theme.id, request),
            firstRepresentation?.let { characterAddedToOpposition(it, valueWeb, oppositionValue) },
            characterInTheme?.let { characterIncludedInTheme(it, themeWithCharacter) }
        )

        output.addedOppositionToValueWeb(response)
    }

    private suspend fun getThemeAndValueWeb(valueWebId: UUID): Pair<Theme, ValueWeb> {
        val theme = themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId)

        return theme to theme.valueWebs.find { it.id.uuid == valueWebId }!!
    }

    private fun includeCharacterInThemeIfNeeded(
        theme: Theme,
        representation: SymbolicRepresentation?
    ): Pair<Theme, CharacterInTheme?> {
        val characterId = representation?.let { Character.Id(it.entityUUID) } ?: return Pair(theme, null)
        return if (theme.containsCharacter(characterId)) {
            theme to null
        } else {
            val updatedTheme = theme.withCharacterIncluded(characterId, representation.name, null)
            updatedTheme to updatedTheme.getIncludedCharacterById(characterId)
        }
    }

    private suspend fun createOpposition(
        theme: Theme,
        valueWeb: ValueWeb,
        name: String?,
        firstRepresentation: SymbolicRepresentation?
    ): OppositionValue {
        if (name != null) validateOppositionValueName(name)
        val oppositionValue = OppositionValue(name ?: "${valueWeb.name} ${valueWeb.oppositions.size + 1}")
        val updatedValueWeb = valueWeb.withOpposition(oppositionValue).let {
            if (firstRepresentation != null) it.withRepresentationOf(firstRepresentation, oppositionValue.id)
            else it
        }

        themeRepository.updateTheme(theme.withReplacedValueWeb(updatedValueWeb))
        return updatedValueWeb.oppositions.find { it isSameEntityAs oppositionValue }!!
    }

    private suspend fun createSymbolicItemIfRequested(
        request: AddOppositionToValueWeb.RequestModel
    ): SymbolicRepresentation? {
        return if (request.firstLinkedItem == null) null
        else {
            val character = getCharacter(request.firstLinkedItem)
            SymbolicRepresentation(character.id.uuid, character.name)
        }
    }

    private suspend fun getCharacter(firstLinkedItem: CharacterId) =
        (characterRepository.getCharacterById(Character.Id(firstLinkedItem.characterId))
            ?: throw CharacterDoesNotExist(firstLinkedItem.characterId))

    private fun oppositionAddedToValueWeb(
        oppositionValue: OppositionValue,
        themeId: Theme.Id,
        request: AddOppositionToValueWeb.RequestModel
    ): OppositionAddedToValueWeb
    {
        return OppositionAddedToValueWeb(
            themeId = themeId.uuid,
            valueWebId = request.valueWebId,
            oppositionValueId = oppositionValue.id.uuid,
            oppositionValueName = oppositionValue.name,
            needsName = request.name == null
        )
    }

    private fun characterAddedToOpposition(
        symbolicRepresentation: SymbolicRepresentation,
        valueWeb: ValueWeb,
        oppositionValue: OppositionValue
    ): SymbolicRepresentationAddedToOpposition
    {
        return CharacterAddedToOpposition(
            themeId = valueWeb.themeId.uuid,
            valueWebId = valueWeb.id.uuid,
            valueWebName = valueWeb.name,
            oppositionId = oppositionValue.id.uuid,
            oppositionName = oppositionValue.name,
            itemName = symbolicRepresentation.name,
            characterId = symbolicRepresentation.entityUUID
        )
    }

    private fun characterIncludedInTheme(characterInTheme: CharacterInTheme, theme: Theme): CharacterIncludedInTheme
    {
        return CharacterIncludedInTheme(theme.id.uuid, theme.name, characterInTheme.id.uuid, characterInTheme.name, false)
    }
}