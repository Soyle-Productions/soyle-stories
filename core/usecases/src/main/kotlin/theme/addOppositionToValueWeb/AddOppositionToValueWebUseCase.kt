package com.soyle.stories.usecase.theme.addOppositionToValueWeb

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.SymbolicRepresentation
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.ValueWebDoesNotExist
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb.*
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.SymbolicItemId
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

class AddOppositionToValueWebUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : AddOppositionToValueWeb {

    @Suppress("OverridingDeprecatedMember")
    override suspend fun invoke(valueWebId: UUID, output: OutputPort) {
        invoke(RequestModel(valueWebId), output)
    }

    override suspend fun invoke(
        request: RequestModel,
        output: OutputPort
    ) {
        val theme = getThemeContainingValueWeb(request.valueWebId)
        val executor = Executor(theme, ValueWeb.Id(request.valueWebId))
            .createOpposition(request.name)
            .createSymbolicItemIfRequested(request.firstLinkedItem)
        persistThemeUpdate(executor.theme)
        output.addedOppositionToValueWeb(executor.response!!)
    }


    private suspend fun getThemeContainingValueWeb(valueWebId: UUID): Theme {
        return themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId)
    }

    private suspend fun persistThemeUpdate(theme: Theme) {
        themeRepository.updateTheme(theme)
    }

    private suspend fun getCharacter(firstLinkedItem: CharacterId) =
        characterRepository.getCharacterOrError(firstLinkedItem.characterId)

    private inner class Executor(
        val theme: Theme,
        val valueWebId: ValueWeb.Id,
        val oppositionValueId: OppositionValue.Id? = null,
        val response: ResponseModel? = null
    ) {

        val valueWeb by lazy {
            theme.valueWebs.find { it.id == valueWebId }!!
        }

        fun createOpposition(name: NonBlankString?): Executor {

            val newValueWeb = valueWeb.withOpposition(name)

            val oppositionValue = newValueWeb.oppositions.last()

            return Executor(
                theme.withReplacedValueWeb(newValueWeb),
                valueWeb.id,
                oppositionValue.id,
                ResponseModel(
                    OppositionAddedToValueWeb(
                        themeId = theme.id.uuid,
                        valueWebId = valueWeb.id.uuid,
                        oppositionValueId = oppositionValue.id.uuid,
                        oppositionValueName = oppositionValue.name.value,
                        needsName = name == null
                    ),
                    null,
                    null,
                    null
                )
            )
        }

        suspend fun createSymbolicItemIfRequested(firstLinkedItem: SymbolicItemId?): Executor {
            return if (firstLinkedItem is CharacterId) {
                val character = getCharacter(firstLinkedItem)
                val executorWithCharacter = includeCharacterInThemeIfNeeded(character)
                val symbolicItem = SymbolicRepresentation(character.id.uuid, character.displayName.value)

                val executorWithoutRepresentation = if (valueWeb.hasRepresentation(symbolicItem.entityUUID)) {
                    executorWithCharacter.removeSymbolicItemFromValueWeb(symbolicItem)
                } else executorWithCharacter

                executorWithoutRepresentation.addSymbolicItem(symbolicItem)
            } else this
        }

        private fun removeSymbolicItemFromValueWeb(symbolicItem: SymbolicRepresentation): Executor {
            val currentOppositionValueWithSymbolicItem =
                valueWeb.oppositions.find { it.hasEntityAsRepresentation(symbolicItem.entityUUID) }!!
            return Executor(
                theme.withReplacedValueWeb(valueWeb.withoutRepresentation(symbolicItem.entityUUID)),
                valueWeb.id,
                oppositionValueId,
                response!!.let {
                    ResponseModel(
                        it.oppositionAddedToValueWeb,
                        RemovedSymbolicItem(
                            theme.id.uuid,
                            valueWeb.id.uuid,
                            currentOppositionValueWithSymbolicItem.id.uuid,
                            symbolicItem.entityUUID
                        ),
                        it.symbolicRepresentationAddedToOpposition,
                        it.characterIncludedInTheme
                    )
                }
            )
        }

        private fun addSymbolicItem(symbolicItem: SymbolicRepresentation): Executor
        {
            return Executor(
                theme.withReplacedValueWeb(valueWeb.withRepresentationOf(symbolicItem, oppositionValueId!!)),
                valueWeb.id,
                oppositionValueId,
                response!!.let {
                    ResponseModel(
                        it.oppositionAddedToValueWeb,
                        it.symbolicRepresentationRemoved,
                        CharacterAddedToOpposition(
                            theme.id.uuid,
                            valueWeb.id.uuid,
                            valueWeb.name.value,
                            it.oppositionAddedToValueWeb.oppositionValueId,
                            it.oppositionAddedToValueWeb.oppositionValueName,
                            symbolicItem.name,
                            symbolicItem.entityUUID
                        ),
                        it.characterIncludedInTheme
                    )
                }
            )
        }

        fun includeCharacterInThemeIfNeeded(character: Character): Executor {
            return if (theme.containsCharacter(character.id)) this
            else {
                Executor(
                    theme.withCharacterIncluded(character.id, character.displayName.value, character.media),
                    valueWebId,
                    oppositionValueId,
                    response!!.let {
                        ResponseModel(
                            it.oppositionAddedToValueWeb,
                            it.symbolicRepresentationRemoved,
                            it.symbolicRepresentationAddedToOpposition,
                            CharacterIncludedInTheme(theme.id.uuid, theme.name, character.id.uuid, character.displayName.value, false)
                        )
                    }
                )
            }
        }
    }

}