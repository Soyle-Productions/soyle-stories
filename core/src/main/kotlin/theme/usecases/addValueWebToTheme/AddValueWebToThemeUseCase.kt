package com.soyle.stories.theme.usecases.addValueWebToTheme

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.entities.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicItemId
import com.soyle.stories.entities.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

class AddValueWebToThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : AddValueWebToTheme {

    override suspend fun invoke(request: AddValueWebToTheme.RequestModel, output: AddValueWebToTheme.OutputPort) {
        val (theme, valueWeb) = getTheme(request.themeId)
            .withValueWeb(request.name)
        val result = Result(theme, valueWeb, addedValueWeb(valueWeb))
        result.addSymbolicItemIfRequested(request)
        save(result)
        output.addedValueWebToTheme(responseModel(result))
    }

    @Suppress("OverridingDeprecatedMember")
    override suspend fun invoke(themeId: UUID, name: String, output: AddValueWebToTheme.OutputPort) {
    }

    private suspend fun getTheme(themeId: UUID) = themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId)

    private suspend fun Result.addSymbolicItemIfRequested(request: AddValueWebToTheme.RequestModel)
    {
        if (request.automaticallyLinkItem == null) return
        else addSymbolicItem(request.automaticallyLinkItem)
    }

    private suspend fun Result.addSymbolicItem(symbolicItemId: SymbolicItemId)
    {
        val representation: SymbolicRepresentation = getRepresentationForItemId(symbolicItemId)
        if (symbolicItemId is CharacterId) {
            makeSureCharacterIsIncludedInTheme(representation.entityUUID, representation.name)
        }
        valueWeb = valueWeb.withRepresentationOf(representation, valueWeb.oppositions.first().id)
        theme = theme.withoutValueWeb(valueWeb.id).withValueWeb(valueWeb)
        symbolicItemAdded =
            CharacterAddedToOpposition(
                theme.id.uuid,
                valueWeb.id.uuid,
                valueWeb.name,
                valueWeb.oppositions.first().id.uuid,
                valueWeb.oppositions.first().name,
                representation.name,
                representation.entityUUID
            )
    }

    private suspend fun getRepresentationForItemId(symbolicItemId: SymbolicItemId): SymbolicRepresentation
    {
        when (symbolicItemId) {
            is CharacterId -> {
                val character = characterRepository.getCharacterById(Character.Id(symbolicItemId.characterId))
                    ?: throw CharacterDoesNotExist(symbolicItemId.characterId)
                return SymbolicRepresentation(character.id.uuid, character.name.value)
            }
            else -> error("unexpected representation type (location and symbol not yet available for auto-linking.")
        }
    }

    private fun Result.makeSureCharacterIsIncludedInTheme(characterId: UUID, characterName: String)
    {
        if (! theme.containsCharacter(Character.Id(characterId))) {
            theme = theme.withCharacterIncluded(Character.Id(characterId), characterName, null)
            includedCharacter = CharacterIncludedInTheme(theme.id.uuid, theme.name, characterId, characterName, false)
        }
    }

    private suspend fun save(result: Result)
    {
        themeRepository.updateTheme(result.theme)
    }

    private fun responseModel(result: Result): AddValueWebToTheme.ResponseModel {
        return AddValueWebToTheme.ResponseModel(
            result.addedValueWeb,
            result.includedCharacter,
            result.symbolicItemAdded
        )
    }

    private fun addedValueWeb(valueWeb: ValueWeb) = ValueWebAddedToTheme(
        valueWeb.themeId.uuid,
        valueWeb.id.uuid,
        valueWeb.name,
        OppositionAddedToValueWeb(
            valueWeb.themeId.uuid,
            valueWeb.id.uuid,
            valueWeb.oppositions.single().id.uuid,
            valueWeb.oppositions.single().name,
            false
        )
    )

    private class Result(
        var theme: Theme,
        var valueWeb: ValueWeb,
        var addedValueWeb: ValueWebAddedToTheme,
        var includedCharacter: CharacterIncludedInTheme? = null,
        var symbolicItemAdded: SymbolicRepresentationAddedToOpposition? = null
    )
}