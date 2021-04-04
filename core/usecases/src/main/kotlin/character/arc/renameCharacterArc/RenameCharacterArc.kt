package com.soyle.stories.usecase.character.arc.renameCharacterArc

import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameCharacterArc {

	class RequestModel(val characterId: UUID, val themeId: UUID, val name: NonBlankString)

	suspend operator fun invoke(request: RequestModel, outputPort: OutputPort)

	class ResponseModel(val characterId: UUID, val themeId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameCharacterArcResponse(response: ResponseModel)
	}

}