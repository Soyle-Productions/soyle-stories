package com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition

import java.util.*

interface ListAvailableEntitiesToAddToOpposition {

    suspend operator fun invoke(oppositionId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableEntitiesListedToAddToOpposition(response: EntitiesAvailableToAddToOpposition)
    }

}