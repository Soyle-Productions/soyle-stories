package com.soyle.stories.character

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

object CharacterSteps {

	fun setNumberOfCharactersCreated(double: SoyleStoriesTestDouble, atLeast: Int)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		val currentCount = getNumberOfCharactersCreated(double)
		runBlocking {
			repeat(atLeast - currentCount) {
				scope.get<CreateCharacterDialogViewListener>().createCharacter("New Character ${UUID.randomUUID()}")
			}
		}
	}

	fun getCharactersCreated(double: SoyleStoriesTestDouble): List<Character>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<CharacterRepository>().listCharactersInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCharactersCreated(double: SoyleStoriesTestDouble): Int = getCharactersCreated(double).size

	fun givenANumberOfCharactersHaveBeenCreated(double: SoyleStoriesTestDouble, atLeast: Int) {
		if (getNumberOfCharactersCreated(double) < atLeast) {
			setNumberOfCharactersCreated(double, atLeast)
		}
		assertTrue(getNumberOfCharactersCreated(double) >= atLeast)
	}

}