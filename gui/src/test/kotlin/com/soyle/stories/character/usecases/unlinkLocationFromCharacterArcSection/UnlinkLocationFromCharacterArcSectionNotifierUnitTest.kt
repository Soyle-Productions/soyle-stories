package com.soyle.stories.character.usecases.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionNotifier
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class UnlinkLocationFromCharacterArcSectionNotifierUnitTest {

	private var forwardedEvent: Any? = null

	@Test
	fun `failure routes to failures`() {
		val outputPort = getOutputPort()
		val failure = object : CharacterArcException() {}
		outputPort.receiveUnlinkLocationFromCharacterArcSectionFailure(failure)
		assertEquals(failure, forwardedEvent)
	}

	@Test
	fun `response routes to responses`() {
		val outputPort = getOutputPort()
		val response = UnlinkLocationFromCharacterArcSection.ResponseModel(UUID.randomUUID())
		outputPort.receiveUnlinkLocationFromCharacterArcSectionResponse(response)
		assertEquals(response, forwardedEvent)
	}

	private fun getOutputPort(): UnlinkLocationFromCharacterArcSection.OutputPort
	{
		return UnlinkLocationFromCharacterArcSectionNotifier().apply {
			addListener(object : UnlinkLocationFromCharacterArcSection.OutputPort {
				override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {
					forwardedEvent = failure
				}

				override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
					forwardedEvent = response
				}
			})
		}
	}

}