package com.soyle.stories.characterarc.changeCentralMoralQuestion

import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import java.util.*

class ChangeCentralMoralQuestionController(
  themeId: String,
  private val changeCentralMoralQuestion: ChangeCentralMoralQuestion,
  private val changeCentralMoralQuestionOutputPort: ChangeCentralMoralQuestion.OutputPort
){

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun updateCentralMoralQuestion(question: String) {
		changeCentralMoralQuestion.invoke(
		  themeId,
		  question,
		  changeCentralMoralQuestionOutputPort
		)
	}
}