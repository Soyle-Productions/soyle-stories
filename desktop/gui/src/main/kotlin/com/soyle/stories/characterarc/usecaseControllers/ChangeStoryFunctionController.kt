package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.usecase.theme.changeStoryFunction.ChangeStoryFunction
import java.util.*

class ChangeStoryFunctionController(
  themeId: String,
  private val changeStoryFunction: ChangeStoryFunction,
  private val changeStoryFunctionOutputPort: ChangeStoryFunction.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun setStoryFunction(
	  characterId: String,
	  targetCharacterId: String,
	  storyFunction: String
	) {
		changeStoryFunction.invoke(
		  ChangeStoryFunction.RequestModel(
			themeId,
			UUID.fromString(characterId),
			UUID.fromString(targetCharacterId),
			ChangeStoryFunction.StoryFunction.valueOf(storyFunction)
		  ),
		  changeStoryFunctionOutputPort
		)
	}
}