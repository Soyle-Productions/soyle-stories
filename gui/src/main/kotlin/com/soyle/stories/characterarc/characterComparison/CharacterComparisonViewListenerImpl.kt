package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.characterarc.usecaseControllers.*
import com.soyle.stories.theme.removeCharacterFromComparison.RemoveCharacterFromComparisonController

class CharacterComparisonViewListenerImpl(
	private val themeId: String,
  private val characterComparisonController: CharacterComparisonController,
  private val changeThematicSectionValueController: ChangeThematicSectionValueController,
  private val includeCharacterInComparisonController: com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonController,
  private val promoteMinorCharacterController: PromoteMinorCharacterController,
  private val deleteLocalCharacterArcController: DeleteLocalCharacterArcController,
  private val changeStoryFunctionController: ChangeStoryFunctionController,
  private val changeCentralMoralQuestionController: ChangeCentralMoralQuestionController,
  private val changeCharacterPropertyController: ChangeCharacterPropertyController,
  private val changeCharacterPerspectivePropertyController: ChangeCharacterPerspectivePropertyController,
  private val removeCharacterFromComparisonController: RemoveCharacterFromComparisonController
) : CharacterComparisonViewListener {

	override suspend fun getCharacterComparison(characterId: String?) {
		characterComparisonController.getCharacterComparison(characterId)
	}

	override suspend fun addCharacterToComparison(characterId: String) {
		includeCharacterInComparisonController.includeCharacterInTheme(themeId, characterId)
	}

	override suspend fun promoteCharacter(characterId: String) {
		promoteMinorCharacterController.promoteCharacter(characterId)
	}

	override suspend fun demoteCharacter(characterId: String) {
		deleteLocalCharacterArcController.demoteCharacter(characterId)
	}

	override suspend fun updateValue(sectionId: String, value: String) {
		changeThematicSectionValueController.changeThematicSectionValue(sectionId, value)
	}

	override suspend fun setStoryFunction(
	  characterId: String,
	  targetCharacterId: String,
	  storyFunction: String
	) {
		changeStoryFunctionController.setStoryFunction(
		  characterId, targetCharacterId, storyFunction
		)
	}

	override suspend fun updateCentralMoralQuestion(question: String) {
		changeCentralMoralQuestionController.updateCentralMoralQuestion(question)
	}

	override suspend fun changeCharacterPropertyValue(
	  characterId: String,
	  property: String,
	  value: String
	) {
		changeCharacterPropertyController.changeCharacterPropertyValue(characterId, property, value)
	}

	override suspend fun changeSharedPropertyValue(
	  perspectiveCharacterId: String,
	  targetCharacterId: String,
	  property: String,
	  value: String
	) {
		changeCharacterPerspectivePropertyController.changeSharedPropertyValue(
		  perspectiveCharacterId, targetCharacterId, property, value
		)
	}

	override suspend fun removeCharacterFromComparison(characterId: String) {
		removeCharacterFromComparisonController.removeCharacter(themeId, characterId)
	}
}