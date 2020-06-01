package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutDoesNotContainFixedTool
import com.soyle.stories.layout.assertLayoutDoesNotExist
import com.soyle.stories.layout.assertResponseModel
import com.soyle.stories.layout.doubles.LayoutRepositoryDouble
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpenedUseCase
import de.jodamob.junit5.SealedClassesSource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest

class ToggleToolOpenUnitTest {

	private val projectId = Project.Id().uuid

	@Test
	fun `layout doesn't exist`() {
		whenToolIsToggled(FixedTool.CharacterList)
		assertLayoutDoesNotExist().invoke(result)
	}

	@Test
	fun `layout doesn't contain fixed tool`() {
		givenLayoutExists()
		whenToolIsToggled(FixedTool.CharacterList)
		assertLayoutDoesNotContainFixedTool(FixedTool.CharacterList).invoke(result)
	}

	@ParameterizedTest
	@SealedClassesSource
	fun `close if open`(fixedTool: FixedTool) {
		givenLayoutExists()
		givenLayoutHasFixedTool(fixedTool, isOpen = true)
		whenToolIsToggled(fixedTool)
		assertResponseModel(savedLayout!!).invoke(result)
	}

	@ParameterizedTest
	@SealedClassesSource
	fun `open if closed`(fixedTool: FixedTool) {
		givenLayoutExists()
		givenLayoutHasFixedTool(fixedTool, isOpen = false)
		whenToolIsToggled(fixedTool)
		assertResponseModel(savedLayout!!).invoke(result)
	}

	private var savedLayout: Layout? = null

	private val layoutRepository = LayoutRepositoryDouble(onSaveLayout = {
		savedLayout = it
	})

	private fun givenLayoutExists() {
		layoutRepository.layout = Layout(Project.Id(projectId))
	}
	private fun givenLayoutHasFixedTool(fixedTool: FixedTool, isOpen: Boolean)
	{
		val layout = layoutRepository.layout!!
		layoutRepository.layout = layout.withToolAddedToStack(Tool(fixedTool, isOpen), layout.primaryStack.id)
	}

	private var result: Any? = null

	private fun whenToolIsToggled(tool: FixedTool)
	{
		val useCase: ToggleToolOpened = ToggleToolOpenedUseCase(projectId, layoutRepository)
		val output = object : ToggleToolOpened.OutputPort {
			override fun receiveToggleToolOpenedResponse(response: GetSavedLayout.ResponseModel) {
				result = response
			}

			override fun failedToToggleToolOpen(failure: Throwable) {
				result = failure
			}
		}
		runBlocking {
			useCase.invoke(tool, output)
		}
	}

	private fun assertLayoutDoesNotContainFixedTool(fixedTool: FixedTool): (Any?) -> Unit = { actual ->
		actual as LayoutDoesNotContainFixedTool
		assertEquals(fixedTool, actual.fixedTool)
	}

}