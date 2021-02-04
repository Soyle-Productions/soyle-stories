package com.soyle.stories.prose.usecases.updateProse

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.repositories.ProseRepository

class UpdateProseUseCase(
    private val proseRepository: ProseRepository
) : UpdateProse {

    override suspend fun invoke(proseId: Prose.Id, content: List<ProseContent>, output: UpdateProse.OutputPort) {
        val prose = proseRepository.getProseById(proseId)
            ?: throw ProseDoesNotExist(proseId)
        val (newProse, event) = prose.withContentReplaced(content)
        proseRepository.replaceProse(newProse)
        proseRepository.addEvents(prose.id, listOf(event))
        output(UpdateProse.ResponseModel(event))
    }

}