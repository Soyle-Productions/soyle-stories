package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.characterarc.usecaseControllers.*

class CharacterComparisonViewListenerImpl(
  private val characterComparisonController: CharacterComparisonController,
  private val changeThematicSectionValueController: ChangeThematicSectionValueController,
  private val includeCharacterInComparisonController: IncludeCharacterInComparisonController,
  private val promoteMinorCharacterController: PromoteMinorCharacterController,
  private val deleteLocalCharacterArcController: DeleteLocalCharacterArcController,
  private val changeStoryFunctionController: ChangeStoryFunctionController,
  private val changeCentralMoralQuestionController: ChangeCentralMoralQuestionController,
  private val changeCharacterPropertyController: ChangeCharacterPropertyController,
  private val changeCharacterPerspectivePropertyController: ChangeCharacterPerspectivePropertyController,
  private val removeCharacterFromLocalComparisonController: RemoveCharacterFromLocalComparisonController
) : CharacterComparisonViewListener {

	override suspend fun getCharacterComparison(characterId: String) {
		characterComparisonController.getCharacterComparison(characterId)
	}

	override suspend fun addCharacterToComparison(characterId: String) {
		includeCharacterInComparisonController.addCharacterToComparison(characterId)
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
		removeCharacterFromLocalComparisonController.removeCharacterFromComparison(characterId)
	}
}