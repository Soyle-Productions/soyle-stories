package com.soyle.stories.usecase.prose.readProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.prose.ProseRepository

class ReadProseUseCase(private val proseRepository: ProseRepository) : ReadProse {
    override suspend fun invoke(proseId: Prose.Id, output: ReadProse.OutputPort) {
        val prose = proseRepository.getProseById(proseId) ?: throw ProseDoesNotExist(proseId)
        output.receiveProse(ReadProse.ResponseModel(
            prose.id,
            prose.revision,
            prose.text,
            prose.mentions
        ))

    }
}