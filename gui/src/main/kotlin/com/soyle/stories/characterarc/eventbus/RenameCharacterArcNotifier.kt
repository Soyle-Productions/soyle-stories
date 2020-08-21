package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer

class RenameCharacterArcNotifier(
	private val threadTransformer: ThreadTransformer
) : RenameCharacterArc.OutputPort, Notifier<RenameCharacterArc.OutputPort>() {

	override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveRenameCharacterArcResponse(response) }
		}
	}
}