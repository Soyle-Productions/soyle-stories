package com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangeCentralMoralQuestion
import java.util.*

class ChangeCentralMoralQuestionControllerImpl(
	private val threadTransformer: ThreadTransformer,
    private val changeCentralMoralQuestion: ChangeCentralMoralQuestion,
    private val changeCentralMoralQuestionOutputPort: ChangeCentralMoralQuestion.OutputPort
) : ChangeCentralMoralQuestionController {

	override fun updateCentralMoralQuestion(themeId: String, question: String) {
		val themeUUID = UUID.fromString(themeId)
		threadTransformer.async {
			changeCentralMoralQuestion.invoke(
				themeUUID,
				question,
				changeCentralMoralQuestionOutputPort
			)
		}
	}
}