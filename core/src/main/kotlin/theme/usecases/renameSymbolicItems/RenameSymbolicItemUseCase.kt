package com.soyle.stories.theme.usecases.renameSymbolicItems

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class RenameSymbolicItemUseCase(
    private val themeRepository: ThemeRepository
) : RenameSymbolicItem {

    override suspend fun invoke(symbolicEntityId: UUID, newName: String, output: RenameSymbolicItem.OutputPort) {
        val themes = themeRepository.getThemeContainingOppositionsWithSymbolicEntityId(symbolicEntityId)

        themeRepository.updateThemes(themes.map {
            it.valueWebs.fold(it) { a, b ->
                a.withoutValueWeb(b.id).withValueWeb(
                    b.withRepresentationRenamedTo(symbolicEntityId, newName)
                )
            }
        })

        output.symbolicItemRenamed(themes.flatMap { theme ->
            theme.valueWebs.flatMap { valueWeb ->
                valueWeb.oppositions.flatMap { opposition ->
                    opposition.representations
                        .filter { it.entityUUID == symbolicEntityId }
                        .map(renamedSymbolicItem(theme, valueWeb, opposition, newName))
                }
            }
        })
    }

    private fun renamedSymbolicItem(
        theme: Theme,
        valueWeb: ValueWeb,
        oppositionValue: OppositionValue,
        newName: String
    ) = fun(representation: SymbolicRepresentation) = RenamedSymbolicItem(
        theme.id.uuid,
        valueWeb.id.uuid,
        oppositionValue.id.uuid,
        representation.entityUUID,
        newName
    )


}