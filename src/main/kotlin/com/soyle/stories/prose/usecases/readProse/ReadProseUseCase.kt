package com.soyle.stories.prose.usecases.readProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.repositories.ProseRepository

class ReadProseUseCase(private val proseRepository: ProseRepository) : ReadProse {
    override suspend fun invoke(proseId: Prose.Id, output: ReadProse.OutputPort) {
        val prose = proseRepository.getProseById(proseId) ?: throw ProseDoesNotExist(proseId)
        output.receiveProse(ReadProse.ResponseModel(
            prose.id,
            prose.revision,
            prose.content,
            prose.mentions
        ))

    }
}