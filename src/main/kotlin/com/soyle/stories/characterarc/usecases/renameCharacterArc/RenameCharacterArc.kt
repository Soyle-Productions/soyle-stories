package com.soyle.stories.characterarc.usecases.renameCharacterArc

import java.util.*

interface RenameCharacterArc {

	class RequestModel(val characterId: UUID, val themeId: UUID, val name: String)

	suspend operator fun invoke(request: RequestModel, outputPort: OutputPort)

	class ResponseModel(val characterId: UUID, val themeId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameCharacterArcFailure(failure: Exception)
		fun receiveRenameCharacterArcResponse(response: ResponseModel)
	}

}