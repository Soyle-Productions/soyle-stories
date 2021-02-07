package com.soyle.stories.usecase.theme.listOppositionsInValueWeb

import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.ValueWebDoesNotExist
import java.util.*

class ListOppositionsInValueWebUseCase(
    private val themeRepository: ThemeRepository
) : ListOppositionsInValueWeb {

    override suspend fun invoke(valueWebId: UUID, output: ListOppositionsInValueWeb.OutputPort) {
        val theme = themeRepository.getThemeContainingValueWebWithId(ValueWeb.Id(valueWebId))
            ?: throw ValueWebDoesNotExist(valueWebId)

        val valueWeb = theme.valueWebs.find { it.id.uuid == valueWebId }!!

        output.oppositionsListedInValueWeb(OppositionsInValueWeb(valueWeb.oppositions.map {
            OppositionValueWithSymbols(it.id.uuid, it.name.value, it.representations.map {
                SymbolicItem(it.entityUUID, it.name)
            })
        }))
    }

}