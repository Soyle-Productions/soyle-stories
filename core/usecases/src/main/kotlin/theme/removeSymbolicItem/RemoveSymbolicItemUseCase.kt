package com.soyle.stories.usecase.theme.removeSymbolicItem

import com.soyle.stories.domain.theme.OppositionValueDoesNotExist
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.theme.SymbolicRepresentationNotInOppositionValue
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RemoveSymbolicItemUseCase(
    private val themeRepository: ThemeRepository
) : RemoveSymbolicItem {

    override suspend fun removeSymbolicItemFromOpposition(
        oppositionId: UUID,
        symbolicItemId: UUID,
        output: RemoveSymbolicItem.OutputPort
    ) {
        val (theme, valueWeb, oppositionValue) = getOppositionValueAndAncestors(oppositionId)

        oppositionValueMustContainItem(oppositionValue, symbolicItemId)

        themeRepository.updateTheme(
            theme.withoutRepresentationsIn(valueWeb, symbolicItemId, oppositionValue)
        )

        output.symbolicItemsRemoved(listOf(RemovedSymbolicItem(theme.id.uuid, valueWeb.id.uuid, oppositionId, symbolicItemId)))
    }

    private suspend fun getOppositionValueAndAncestors(oppositionId: UUID): Triple<Theme, ValueWeb, OppositionValue> {
        val theme = themeRepository.getThemeContainingOppositionValueWithId(OppositionValue.Id(oppositionId))
            ?: throw OppositionValueDoesNotExist(oppositionId)
        val valueWeb = theme.valueWebs.find { it.oppositions.any { it.id.uuid == oppositionId } }!!
        val oppositionValue = valueWeb.oppositions.find { it.id.uuid == oppositionId }!!
        return Triple(theme, valueWeb, oppositionValue)
    }

    private fun oppositionValueMustContainItem(
        oppositionValue: OppositionValue,
        symbolicItemId: UUID
    ) {
        if (oppositionValue.representations.none { it.entityUUID == symbolicItemId }) {
            throw SymbolicRepresentationNotInOppositionValue(oppositionValue.id.uuid, symbolicItemId)
        }
    }

    override suspend fun removeSymbolicItemFromAllThemes(symbolicItemId: UUID, output: RemoveSymbolicItem.OutputPort) {
        val themes = themeRepository.getThemeContainingOppositionsWithSymbolicEntityId(symbolicItemId)

        themeRepository.updateThemes(themes.map {
            it.valueWebs.fold(it) { theme, web ->
                theme.withoutRepresentationsIn(web, symbolicItemId)
            }
        })

        output.symbolicItemsRemoved(themes.flatMap { theme ->
            theme.valueWebs.flatMap { valueWeb ->
                valueWeb.oppositions.flatMap { oppositionValue ->
                    oppositionValue.representations
                        .filter { it.entityUUID == symbolicItemId }
                        .map {
                            RemovedSymbolicItem(
                                theme.id.uuid,
                                valueWeb.id.uuid,
                                oppositionValue.id.uuid,
                                symbolicItemId
                            )
                        }
                }
            }
        })
    }

    private fun Theme.withoutRepresentationsIn(valueWeb: ValueWeb, entityId: UUID, oppositionHint: OppositionValue? = null): Theme {
        val withoutWeb = withoutValueWeb(valueWeb.id)
        return if (oppositionHint != null) withoutWeb.withValueWeb(valueWeb.withoutRepresentationIn(entityId, oppositionHint.id))
        else withoutWeb.withValueWeb(valueWeb.oppositions.fold(valueWeb) { web, it ->
            web.withoutRepresentationIn(entityId, it.id)
        })
    }


    private fun OppositionValue.withoutRepresentationsFor(entityId: UUID) = representations.fold(this) { op, rep ->
        if (rep.entityUUID == entityId) op.withoutRepresentation(rep.entityUUID)
        else op
    }

}