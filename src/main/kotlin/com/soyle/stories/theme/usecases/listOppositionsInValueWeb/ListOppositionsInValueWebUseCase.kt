package com.soyle.stories.theme.usecases.listOppositionsInValueWeb

import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ListOppositionsInValueWebUseCase(
    private val themeRepository: ThemeRepository
) : ListOppositionsInValueWeb {

    override suspend fun invoke(valueWebId: UUID, output: ListOppositionsInValueWeb.OutputPort) {
        val theme = themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId)

        val valueWeb = theme.valueWebs.find { it.id.uuid == valueWebId }!!

        output.oppositionsListedInValueWeb(OppositionsInValueWeb(valueWeb.oppositions.map {
            OppositionValueItem(it.id.uuid, it.name, it.representations.map {
                SymbolicItem(it.entityUUID, it.name)
            })
        }))
    }

}