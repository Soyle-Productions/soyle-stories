package com.soyle.stories.usecase.theme.listOppositionsInValueWeb

import java.util.*

interface ListOppositionsInValueWeb {

    suspend operator fun invoke(valueWebId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun oppositionsListedInValueWeb(response: OppositionsInValueWeb)
    }

}