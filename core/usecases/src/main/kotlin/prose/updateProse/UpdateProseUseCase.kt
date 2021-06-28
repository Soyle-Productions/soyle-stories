package com.soyle.stories.usecase.prose.updateProse

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.ProseContent
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.prose.ProseRepository

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