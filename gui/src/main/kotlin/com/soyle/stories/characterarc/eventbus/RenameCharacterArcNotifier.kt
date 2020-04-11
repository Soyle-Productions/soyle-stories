package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.eventbus.Notifier

class RenameCharacterArcNotifier : RenameCharacterArc.OutputPort, Notifier<RenameCharacterArc.OutputPort>() {

	override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
		notifyAll { it.receiveRenameCharacterArcResponse(response) }
	}

	override fun receiveRenameCharacterArcFailure(failure: Exception) {
		notifyAll { it.receiveRenameCharacterArcFailure(failure) }
	}
}